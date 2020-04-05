import $file.Common, Common._
import $file.Transformers, Transformers._
import $file.Frontmatter, Frontmatter._
import ammonite.ops._

@main
def main(postTitle: String = "") = {
  if (postTitle.isEmpty) {
    println("Generating HTML pages...")
    BlaarghWriter.generate()
  } else {
    println(s"Perparing new markdown post: $postTitle")
    val fm = FrontMatter(
      title = postTitle,
      author = "",
      date = Some(java.time.LocalDate.now),
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
