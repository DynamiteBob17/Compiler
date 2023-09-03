package analysis;

import java.util.List;
import java.util.Optional;

public class Consumer<T> {

    private final List<T> items;
    protected int index;

    public Consumer(List<T> items) {
        this.items = items;
    }

    protected Optional<T> peek(int ahead) {
        if (index + ahead >= items.size()) {
            return Optional.empty();
        } else {
            return Optional.of(items.get(index + ahead));
        }
    }

    protected Optional<T> peek() {
        return peek(0);
    }

    protected T consume() {
        return items.get(index++);
    }

}
