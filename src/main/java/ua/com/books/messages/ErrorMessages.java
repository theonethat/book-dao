package ua.com.books.messages;

public enum ErrorMessages {

    ERROR_SAVING_LIST("Error saving list of %s"),
    ERROR_SAVING_ENTITY("Error saving %s: %s"),
    ENTITY_NOT_CREATED("%s %s was not created"),
    CANNOT_OBTAIN_ID("Cannot obtain a %s ID"),
    BOOK_DOES_NOT_EXIST("Book with id = %d does not exist"),
    BOOK_WITHOUT_ID("Cannot find a book without ID");

//    CANNOT_PREPARE_INSERT("Cannot prepare statement to insert Book"),
//    CANNOT_PREPARE_SELECT_BY_ID("Cannot prepare statement to select Book by id"),
//    CANNOT_PREPARE_UPDATE("Cannot prepare statement to update Book with id = %d"),
//    CANNOT_PREPARE_REMOVE("Cannot prepare statement to delete Book with id = %d"),

    private String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String formatMessage(Object... params) {
        return String.format(message, params);
    }

    public String formatMessageWithClassName(Object entity) {
        return String.format(message, entity.getClass().getSimpleName(), entity);
    }
}
