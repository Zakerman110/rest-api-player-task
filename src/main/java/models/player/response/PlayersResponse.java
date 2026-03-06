package models.player.response;

import java.util.List;
import java.util.Objects;

public class PlayersResponse {

    private List<PlayerSummaryResponse> players;

    public List<PlayerSummaryResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerSummaryResponse> players) {
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayersResponse)) return false;
        PlayersResponse that = (PlayersResponse) o;
        return Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players);
    }

    @Override
    public String toString() {
        return "PlayersResponse{" +
                "players=" + players +
                '}';
    }
}
