package Merge.model.vos;

public class ConflictNode {

     private int branch;

     private String path;

     public int getBranch() {
          return branch;
     }

     public void setBranch(int branch) {
          this.branch = branch;
     }

     public String getPath() {
          return path;
     }

     public void setPath(String path) {
          this.path = path;
     }

     public ConflictNode(int branch, String path) {
          super();
          this.branch = branch;
          this.path = path;
     }

     
     
}
