package constants;

public enum UserTypes {
    NORMAL("normal"),
    ADMIN( "admin"),
    OBSERVER("observer");

    public String type;

    UserTypes(String type) {
        this.type = type;
    }
}
