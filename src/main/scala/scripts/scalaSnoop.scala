// import java.nio.file.StandardWatchEventKinds._
// import java.nio.file._

// import scala.collection.JavaConversions._

// val watcher = FileSystems.getDefault.newWatchService

// def registerAll(path: String) = {
//  Files.walkFileTree(FileSystems.getDefault.getPath(path), new SimpleFileVisitor[Path] {
//    override preVisitDirectory(dir: Path) = {
//      register(dir)
//      FileVisitResult.CONTINUE
//    }
//  })
// }

// def register(path: String) = {
//   FileSystems.getDefault.getPath(path)
//     .register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
// }

// def watchAlways = {
//   while (true) {
//     val watchKey = watcher.take()
//     watchKey.pollEvents() foreach { event =>
//       val relativePath = event.context.asInstanceOf[Path]
//       val path = watchKey.watchable.asInstanceOf[Path].resolve(relativePath)
//       println(s"Context: ${event.context}")
//       println(s"Relative Path: $relativePath")
//       println(s"Path: $path")
//       println(s"Event: ${event.kind}")
//     }
//     watchKey.reset
//   }
// }
