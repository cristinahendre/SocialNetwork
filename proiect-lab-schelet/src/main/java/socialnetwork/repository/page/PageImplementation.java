package socialnetwork.repository.page;

import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;

import java.util.stream.Stream;

public class PageImplementation<T> extends Page<T> {
    private Pageable pageable;
    private Stream<T> content;

    PageImplementation(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

}
