import $file.Frontmatter, Frontmatter._
import $file.Common, Common._
import ammonite.ops._
import scala.util.Try
import javax.swing._, java.awt._, java.awt.event._
import javax.swing.filechooser.FileFilter

object CreateFrame {

  class ImageFilter extends FileFilter {
    val imgFormats = Seq("tiff", "tif", "jpeg", "jpg", "gif", "png", "svg")

    def getExtension(f: java.io.File): Option[String] =
      Option(f.getName).flatMap { s =>
        val idx = s.lastIndexOf('.')
        if (idx > 0 && idx < s.length -1)
          Option(s.substring(idx+1).toLowerCase())
        else None
      }

    //Accept all directories and all gif, jpg, tiff, or png files.
    def accept(f: java.io.File): Boolean =
      if (f.isDirectory) true else getExtension(f).exists(imgFormats.contains)

    //The description of this filter
    def getDescription = "Just Images"
  }

  val frame = new JFrame("New Article")

  val labelTitle = new JLabel("Title: ")
  val labelAuthor = new JLabel("Author: ")
  val labelIngress = new JLabel("Ingress: ")
  val labelLabels = new JLabel("Labels: ")
  val labelImage = new JLabel("Preview image: ")

  val txtTitle = new JTextField(50)
  val txtAuthor = new JTextField(50)
  val txtIngress = new JTextArea(5, 40)
  val txtLabels = new JTextField(50)

  // =====  FileChooser stuff =====
  val labelSelectedFile = new JLabel("Selected file: ")
  val selectedFile = new JLabel("No file selected.")
  val fileImage = new JFileChooser("/")
  fileImage.addChoosableFileFilter(new ImageFilter())
  fileImage.setAcceptAllFileFilterUsed(false)

  val addImageBtn = new JButton("Add Image")
  addImageBtn.addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) = {
      val res = fileImage.showOpenDialog(frame)
      if (res == JFileChooser.APPROVE_OPTION)
        selectedFile.setText(Option(fileImage.getSelectedFile.getName).getOrElse(""))
    }
  })

  // =====  Labels and Fields =====
  val labelsFields = Seq(
    (labelTitle, txtTitle),
    (labelAuthor, txtAuthor),
    (labelIngress, txtIngress),
    (labelLabels, txtLabels),
    (labelImage, addImageBtn),
    (labelSelectedFile, selectedFile)
  )

  def build() = {
    val inputPanel = new JPanel(new GridBagLayout())
    val constraints = new GridBagConstraints()
    constraints.anchor = GridBagConstraints.WEST
    constraints.insets = new Insets(10, 10, 10, 10)

    labelsFields.zipWithIndex.foreach { lf =>
      val lbl = lf._1._1
      val fld = lf._1._2
      constraints.gridx = 0
      constraints.gridy = lf._2
      inputPanel.add(lbl, constraints)

      constraints.gridx = 1
      inputPanel.add(fld, constraints)
    }

    val doneButton = new JButton("Create")
    doneButton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) = {
        handleDoneButton(e)
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
      }
    })
    val cancelButton = new JButton("Cancel")
    cancelButton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) =
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    })

    val btnPanel = new JPanel(new GridBagLayout())
    val btnConstraints = new GridBagConstraints()
    btnConstraints.anchor = GridBagConstraints.WEST
    btnConstraints.gridx = 0
    btnConstraints.gridy = 0
    btnPanel.add(cancelButton, btnConstraints)
    btnConstraints.gridx = 1
    btnPanel.add(doneButton, btnConstraints)

    constraints.gridx = 0
    constraints.gridy = labelsFields.length + 1
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.CENTER
    inputPanel.add(btnPanel, constraints)

    frame.getContentPane.add(inputPanel)
    frame.pack()
    frame.setVisible(true)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }

  def handleDoneButton(e: ActionEvent) = {
    val maybeFileName = Option(fileImage.getSelectedFile).map { f =>
      val destFile = postsFolder / f.getName
      cp(Path(f), destFile)
      f.getName
    }

    val fm = FrontMatter(
      title = txtTitle.getText,
      author = txtAuthor.getText,
      date = Some(java.time.LocalDate.now),
      ingress = Option(txtIngress.getText),
      labels = Option(txtLabels.getText).map[Seq[String]](_.split(",").map(_.trim()).toSeq),
      image = maybeFileName,
      misc = Map.empty[String, Any]
    )
    write(
      postsFolder / s"${fm.title.replaceAll(" ", "_")}.md",
      fm.toYaml
    )
  }

}

Try {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
}.recover {
  case ex: Exception => ex.printStackTrace()
}

SwingUtilities.invokeLater(new Runnable() {
  def run(): Unit = {
    CreateFrame.build()
  }
})
