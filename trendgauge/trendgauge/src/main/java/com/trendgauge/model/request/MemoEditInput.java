package com.trendgauge.model.request;

public class MemoEditInput {
    private Long memoId;
    private String comment;

    public MemoEditInput(Long memoId, String comment) {
        this.memoId = memoId;
        this.comment = comment;
    }

    public Long getMemoId() {
        return memoId;
    }

    public void setMemoId(Long memoId) {
        this.memoId = memoId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
