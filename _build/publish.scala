load.module(ammonite.ops.cwd/"transformers.scala")
load.module(ammonite.ops.cwd/"frontmatter.scala")
load.module(ammonite.ops.cwd/"common.scala")
@
import ammonite.ops._

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
