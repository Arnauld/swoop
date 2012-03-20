package swoop.path;

import swoop.path.Verb.Category;


public interface VerbMatcher {

    boolean matches(Verb verb);

    boolean belongsTo(Category category);
}
