package endpoints;

public final class PlayerEndpoints {

    private PlayerEndpoints() {}

    public static final String CREATE_PLAYER = "/player/create/{editor}";
    public static final String DELETE_PLAYER = "/player/delete/{editor}";
    public static final String GET_PLAYER_BY_ID = "/player/get";
    public static final String GET_ALL_PLAYERS = "/player/get/all";
    public static final String UPDATE_PLAYER = "/player/update/{editor}/{id}";
}
