package dao;
import beans.Comment;
import beans.Factory;
import beans.ShoppingCart;
import beans.enums.CommentStatus;
import beans.enums.PurchaseStatus;
import beans.roles.Manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class CommentDAO {
    private ArrayList<Comment> comments = new ArrayList<>();
    private String contextPath;
    private ManagerDAO managerDAO;

    public CommentDAO(String contextPath) {
        this.contextPath = contextPath;
        loadComments();
        managerDAO = new ManagerDAO(contextPath);
    }

    public ArrayList<Comment> getAllComments() {
		loadComments();
        return comments;
    }

    public void saveComment(Comment comment) {
        // Generate new ID based on the last comment ID + 1
        int newId = comments.isEmpty() ? 0 : comments.get(comments.size() - 1).getId() + 1;
        comment.setId(newId);
        comment.setStatus(CommentStatus.Processing);
        comments.add(comment);
        saveCommentsToFile();
    }
    
   

    private void saveCommentsToFile() {
        try {
            String filePath = contextPath + "/comments.txt";
            FileWriter writer = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            Comment lastComment = comments.get(comments.size() - 1);
            bufferedWriter.write(lastComment.getId() + ";" +
                    lastComment.getCustomerId() + ";" +
                    lastComment.getFactoryId() + ";" +
                    lastComment.getComment() + ";" +
                    lastComment.getRating() + ";" +
                    lastComment.getStatus() + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Comment> findManagersFactoryComments(String username) {
    	ArrayList<Comment> compatibileComments = new ArrayList<Comment>();
    	loadComments();
        Manager manager = managerDAO.getByUsername(username);
    	for(Comment comment : comments) {
    		if(comment.getFactoryId() == manager.getFactoryId()) {
    	    	compatibileComments.add(comment);
    		}
    	}
    	
    	return compatibileComments;
    }
    

    public ArrayList<Comment> findCommentsByFactoryId(int factoryId) {
        ArrayList<Comment> factoryComments = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getFactoryId() == factoryId) {
                factoryComments.add(comment);
            }
        }
        return factoryComments;
    }

    public void saveAll() {
		try {
	        String filePath = contextPath + "comments.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Comment comment : comments) {
	        	bufferedWriter.write(comment.getId() + ";" +
	        			comment.getCustomerId() + ";" +
	        			comment.getFactoryId() + ";" +
	        			comment.getComment() + ";" +
	        			comment.getRating() + ";" +
	        			comment.getStatus() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
    public Comment updateCommentStatus(int id, String status) {
    	loadComments();
    	for(Comment comment : comments) {
    		if(comment.getId() == id) {
    			comment.setStatus(CommentStatus.valueOf(status));
    			saveAll();
    			loadComments();
    			return comment;
    		}
    	}
    	
    	return null;
    }
   
    

    private void loadComments() {
        this.comments = new ArrayList<>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/comments.txt");
            System.out.println("Loading comments from: " + file.getAbsolutePath());
            in = new BufferedReader(new FileReader(file));
            String line;
            StringTokenizer st;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                st = new StringTokenizer(line, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int customerId = Integer.parseInt(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                String commentText = st.nextToken().trim();
                int rating = Integer.parseInt(st.nextToken().trim());
                CommentStatus status = CommentStatus.valueOf(st.nextToken().trim());
                comments.add(new Comment(id, customerId, factoryId, commentText, rating, status));
            	System.out.println("dodao jedan komentarcic " + comments);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
