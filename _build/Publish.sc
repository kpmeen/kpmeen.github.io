val workingDir = ammonite.ops.pwd

import $file.Common, Common._
import $file.Transformers, Transformers._
import $file.Frontmatter, Frontmatter._
import ammonite.ops._

@main
def main(postTitle: String = "") = {
  if (postTitle.isEmpty) {
    BlaarghWriter.generate()
  } else {
    val fm = FrontMatter(
      title = postTitle,
      author = "",
      date = Some(new java.util.Date()),
      ingress = None,
      labels = None,
      image = None,
      misc = Map.empty[String, Any]
    )

    write(
      postsFolder / s"${postTitle.replaceAll(" ", "_")}.md",
      fm.toYaml
    )

  }
}
