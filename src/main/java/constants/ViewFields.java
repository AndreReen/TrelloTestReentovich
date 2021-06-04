package constants;

public enum ViewFields {
    ID_ORGANIZATION("idOrganization"),
    STARRED("starred"),
    NAME("name");

    public String field;

    ViewFields(String field) {
        this.field = field;
    }
}
