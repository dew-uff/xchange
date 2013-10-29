package Merge.model.vos;

import java.util.HashMap;
import java.util.Map;

public class Conflict {
     public static final String CONFLICT_UPDATE_UPDATE = "Update/Update";
     public static final String CONFLICT_LOCKED_DELETE = "Locked/Delete";
     public static final String CONFLICT_FARMOVE_DELETE = "Far Move/Delete";
     public static final String CONFLICT_FARMOVE_NEARMOVE = "Far Move/Near Move";
     public static final String CONFLICT_NEARMOVE_NEARMOVE = "Near Move/Near Move";
     public static final String CONFLICT_FARMOVE_FARMOVE = "Far Move/Far Move";
     public static final String CONFLICT_NEARMOVE_DELETE = "Near Move/Delete";
     
     public static final String WARNING_EQUAL_UPDATES = "Equal Updates";
     public static final String WARNING_EQUAL_INSERTS = "Equal Inserts";
     public static final String WARNING_INSERTS = "Different Inserts";
     public static final String WARNING_DELETE_UPDATE = "Delete/Update";
     
     public static final int CONFLICT = 0;
     
     public static final int WARNING = 1;

     private Map<Integer,ConflictNode> conflictNodes = new HashMap<Integer, ConflictNode>();
     
     private String description;
     
     private String name;

     private String type;
     
     private int defaultBranch;
     
     private int selectedBranch;
     
     private int conflictWarning;
     
     private String statusno1, statusno2;

     public int getSelectedBranch() {
          return selectedBranch;
     }

     public void setSelectedBranch(int selectedBranch) {
          this.selectedBranch = selectedBranch;
     }

     public String getType() {
          return type;
     }

     public void setType(String type) {
          this.type = type;
     }

     protected Map<Integer, ConflictNode> getConflictNodes() {
          return conflictNodes;
     }

     public String getDescription() {
          return description;
     }

     public void setDescription(String description) {
          this.description = description;
     }
     
     @Override
     public String toString() {
          return (conflictWarning == CONFLICT ? "Conflict " : "Warning ") + name;
     }

     public int getDefaultBranch() {
          return defaultBranch;
     }

     public void setDefaultBranch(int defaultBranch) {
          this.defaultBranch = defaultBranch;
     }
     
     public ConflictNode getConflictNode(int branch) {
          return getConflictNodes().get(new Integer(branch));
     }

     public void setConflictNode(int branch, ConflictNode node) {
          getConflictNodes().put(new Integer(branch), node);
     }

     public int getConflictWarning() {
          return conflictWarning;
     }

     public void setConflictWarning(int conflictWarning) {
          this.conflictWarning = conflictWarning;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusno1() {
        return statusno1;
    }

    public void setStatusno1(String statusno1) {
        this.statusno1 = statusno1;
    }

    public String getStatusno2() {
        return statusno2;
    }

    public void setStatusno2(String statusno2) {
        this.statusno2 = statusno2;
    }
     
     
}
