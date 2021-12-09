package net.javaguides.springboot.springsecurity.converter;

import javax.annotation.processing.Generated;
import net.javaguides.springboot.springsecurity.model.entity.Comment;
import net.javaguides.springboot.springsecurity.model.response.CommentResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-06T12:52:34+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 15.0.5 (Azul Systems, Inc.)"
)
@Component
public class CommentConverterImpl implements CommentConverter {

    @Override
    public CommentResponse convertToToResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setEmail( comment.getEmail() );
        commentResponse.setMessage( comment.getMessage() );

        return commentResponse;
    }
}
