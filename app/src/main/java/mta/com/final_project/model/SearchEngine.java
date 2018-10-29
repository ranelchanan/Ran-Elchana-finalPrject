package mta.com.final_project.model;

public class SearchEngine {
    private String UserId;
    private SearchParams serachParams;

    public String getUserId() {
        return UserId;
    }

    public SearchParams getSerachParams() {
        return serachParams;
    }

    public void setSerachParams(SearchParams serachParams) {
        this.serachParams = serachParams;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}

