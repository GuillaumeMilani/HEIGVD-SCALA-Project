package controllers

import javax.inject._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val title = "Ultimate HEIG-VD Manager 2018"

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        //        routes.javascript.StudentsController.getStudents,
        //        routes.javascript.StudentsController.createStudent,
        //        routes.javascript.StudentsController.getStudent,
        //        routes.javascript.StudentsController.updateStudent,
        //        routes.javascript.StudentsController.deleteStudent,
        //        routes.javascript.CoursesController.getCourses,
        //        routes.javascript.CoursesController.createCourse,
        //        routes.javascript.CoursesController.getCourse,
        //        routes.javascript.CoursesController.updateCourse,
        //        routes.javascript.CoursesController.deleteCourse
      )
    ).as("text/javascript")
  }

  def index = Action {
    Ok(views.html.welcome("Coucou copain"))
  }

  /**
    * Call the "about" html template.
    */
  def about = Action {
    Ok(views.html.about(title))
  }
}
