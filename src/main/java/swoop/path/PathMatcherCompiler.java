package swoop.path;

public interface PathMatcherCompiler {
    PathPatternMatcher compile(String uriExpr);
}
