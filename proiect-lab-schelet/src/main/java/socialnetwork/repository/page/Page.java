package socialnetwork.repository.page;

import java.util.List;
import java.util.stream.Stream;

public class Page<E> {
    Iterable<E> content;
    int totalCount;

    public Iterable<E> getContent() {
        return content;
    }

    public void setContent(Iterable<E> content) {
        this.content = content;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
