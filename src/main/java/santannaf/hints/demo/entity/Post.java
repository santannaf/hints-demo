package santannaf.hints.demo.entity;

public record Post(
        long id,
        String title,
        String userId,
        String body
) {
}
