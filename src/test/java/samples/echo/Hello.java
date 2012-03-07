package samples.echo;

import static swoop.Swoop.get;
import swoop.Action;
import swoop.Request;
import swoop.Response;

public class Hello {

    public static void main(String[] args) {
        get(new Action() {
            @Override
            public void handle(Request request, Response response) {
                response.body("<h1>Hello!</h1>");
            }
        });
    }
}
