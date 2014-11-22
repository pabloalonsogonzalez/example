package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import models.Person;
import play.cache.Cache;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result addPerson() {
    	Person person = Form.form(Person.class).bindFromRequest().get();

    	Person.create(person);

		Cache.set(person.id, person);

		Cache.remove("users");

		sendMail(person);

    	return redirect(routes.Application.index());
    }

	public static Result deletePerson(Long id) {
		Person.delete(id);

		Cache.remove(String.valueOf(id));
		Cache.remove("users");

		return redirect(routes.Application.index());
	}

    public static Result getPersons() {
		List<Person> persons = (List<Person>)(Cache.get("users"));

		if (persons == null) {
			persons = new Model.Finder(String.class, Person.class).all();

			Cache.set("users", persons);
		}

    	return ok(toJson(persons));
    }

	private static void sendMail(Person person) {
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.setSubject("mailer");
		mail.setRecipient("Miguel Ángel Pastor Olivar <miguelinlas3@gmail.com>","miguelinlas3@gmail.com");
		mail.setFrom("Miguel Ángel Pastor Olivar <noreply@email.com>");
//adds attachment
//		mail.addAttachment("attachment.pdf", new File("/some/path/attachment.pdf"));
//adds inline attachment from byte array
//		byte[] data = "data".getBytes();
//		mail.addAttachment("data.txt", data, "text/plain", "A simple file", EmailAttachment.INLINE);
//sends html
//		mail.sendHtml("<html>html</html>" );
//sends text/text
		mail.send( "Welcome to the system " + person.name );
//sends both text and html
//		mail.send( "text", "<html>html</html>");
	}
}
