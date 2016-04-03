load.module(ammonite.ops.cwd/"frontmatter.scala")
@
import ammonite.ops._
import scala.util.Try
import javax.swing._, java.awt._, java.awt.event._

object CreateFrame {

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
  val txtImage = new JTextField(50) // Use https://docs.oracle.com/javase/7/docs/api/javax/swing/JFileChooser.html

  val labelsFields = Seq(
    (labelTitle, txtTitle),
    (labelAuthor, txtAuthor),
    (labelIngress, txtIngress),
    (labelLabels, txtLabels),
    (labelImage, txtImage)
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
      def actionPerformed(e: ActionEvent) =
         handleDoneButton(e)
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
    constraints.gridy = labelsFields.length
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.CENTER
    inputPanel.add(btnPanel, constraints)

    frame.getContentPane.add(inputPanel)
    frame.pack()
    frame.setVisible(true)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }

  def handleDoneButton(e: ActionEvent) = {
    val fm = FrontMatter(
      title = txtTitle.getText,
      author = txtAuthor.getText,
      date = Some(new java.util.Date()),
      ingress = Option(txtIngress.getText),
      labels = None,
      image = Option(txtImage.getText),
      misc = Map.empty[String, Any]
    )
    println(fm)
  }

}

def main() {
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
}
