package result;

import java.util.Collection;

public record ListGamesResult(Collection<ImportantGameInfo> games) {
}
