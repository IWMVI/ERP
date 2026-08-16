package iwmvi.erp.shared.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PageView<T> {

    public static final int PAGE_SIZE = 10;

    private final List<T> items;
    private final int page;
    private final int totalPages;
    private final long totalItems;
    private final String basePath;
    private final Map<String, String> parameters;

    private PageView(
            List<T> items,
            int page,
            int totalPages,
            long totalItems,
            String basePath,
            Map<String, String> parameters) {
        this.items = items;
        this.page = page;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.basePath = basePath;
        this.parameters = parameters;
    }

    public static <T> PageView<T> of(
            List<T> source, int requestedPage, String basePath, Map<String, ?> parameters) {
        int totalItems = source.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) PAGE_SIZE));
        int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
        int from = Math.min(page * PAGE_SIZE, totalItems);
        int to = Math.min(from + PAGE_SIZE, totalItems);

        Map<String, String> normalized = new LinkedHashMap<>();
        parameters.forEach((key, value) -> {
            if (value != null && !value.toString().isBlank()) {
                normalized.put(key, value.toString());
            }
        });

        return new PageView<>(
                List.copyOf(source.subList(from, to)),
                page,
                totalPages,
                totalItems,
                basePath,
                Map.copyOf(normalized));
    }

    public static <T> PageView<T> of(List<T> source, int requestedPage, String basePath) {
        return of(source, requestedPage, basePath, Map.of());
    }

    public List<T> items() {
        return items;
    }

    public int page() {
        return page;
    }

    public int displayPage() {
        return page + 1;
    }

    public int totalPages() {
        return totalPages;
    }

    public long totalItems() {
        return totalItems;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page + 1 < totalPages;
    }

    public String previousUrl() {
        return url(page - 1);
    }

    public String nextUrl() {
        return url(page + 1);
    }

    public String url(int targetPage) {
        int safePage = Math.max(0, Math.min(targetPage, totalPages - 1));
        StringBuilder url = new StringBuilder(basePath).append("?page=").append(safePage);
        parameters.forEach((key, value) -> url.append('&')
                .append(encode(key))
                .append('=')
                .append(encode(value)));
        return url.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
