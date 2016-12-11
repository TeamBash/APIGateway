package iu.edu.teambash;

/**
 * Created by janakbhalla on 11/12/16.
 */
public class JobEntity {
    private int uid;
    private String jobname;

    public JobEntity(int uid, String jobname) {
        this.uid = uid;
        this.jobname = jobname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }
}
