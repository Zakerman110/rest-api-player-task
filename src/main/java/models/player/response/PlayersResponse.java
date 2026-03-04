package models.player.response;

import java.util.List;

public class PlayersResponse {

    private List<PlayerSummaryResponse> players;

    public List<PlayerSummaryResponse> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerSummaryResponse> players) {
        this.players = players;
    }
}
