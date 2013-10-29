package Merge.model.loghandling;

public class NodeConstants {

     public static final int STATUS_OK = 0;
     public static final int STATUS_INSERTED = 1;
     public static final int STATUS_UPDATED = 2;
     public static final int STATUS_DELETED = 3;
     public static final int STATUS_COPIED = 4;
     public static final int STATUS_MOVED_OUT = 5;
     public static final int STATUS_MOVED_IN = 6;
     public static final int BRANCH_B = 0;
     public static final int BRANCH_M1 = 1;
     public static final int BRANCH_M2 = 2;
     public static final int BRANCH_USER = 3;
     public static final int BRANCH_MERGED = 4;
     public static final int TYPE_ELEMENT = 0;
     public static final int TYPE_ATTIBUTE = 1;
     public static final int TYPE_TEXT = 2;

     public NodeConstants() {
          super();
     }

}