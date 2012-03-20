package swoop.pipeline;

import static swoop.path.Verb.Get;
import static swoop.path.VerbMatchers.httpMethods;

import org.testng.annotations.Test;

public class Swoop2Test extends Swoop2Builder {

    @Test
    public void defineRoutes() {
        on(httpMethods, "*", new PipelineUpstreamHandler() {
            @Override
            public void handleUpstream(Pipeline pipeline) {
            }
        });
        on(httpMethods, "*", new PipelineUpAndDownstreamHandler() {
            @Override
            public void handleDownstream(Pipeline pipeline) {
                pipeline.with(new Chrono().start());
            }

            @Override
            public void handleUpstream(Pipeline pipeline) {
                Chrono chrono = pipeline.get(Chrono.class);
                System.out.println(chrono.elapsed() + "ms");
                pipeline.invokeNext();
            }
        });
        on(Get, "/books/all", new PipelineTargetHandler() {
            @Override
            public void handleTarget(Pipeline pipeline) {
            }
        });
    }

    static class Chrono {
        long start, end;

        public Chrono start() {
            this.start = System.currentTimeMillis();
            return this;
        }

        public Chrono end() {
            this.end = System.currentTimeMillis();
            return this;
        }

        public long elapsed() {
            return end - start;
        }
    }

}
