package Comment;


import java.util.EventListener;

public interface CommentListener extends EventListener {

	void addComment(String commentText);
	void removeComment();
}
