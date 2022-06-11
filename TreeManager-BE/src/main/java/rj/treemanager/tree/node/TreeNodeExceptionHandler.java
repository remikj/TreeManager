package rj.treemanager.tree.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import rj.treemanager.tree.node.exceptions.BadActionException;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;

@ControllerAdvice
@Slf4j
public class TreeNodeExceptionHandler {

    @ExceptionHandler(value = {TreeNodeNotFoundException.class, EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFoundException(Exception e){
        log.error("Not Found exception thrown: ", e);
        return e.getMessage();
    }

    @ExceptionHandler(value = {BadActionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBadActionException(Exception e) {
        log.error("Action cannot be done: ", e);
        return e.getMessage();
    }
}
