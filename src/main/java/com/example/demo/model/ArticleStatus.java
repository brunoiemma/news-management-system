package com.example.demo.model;

public enum ArticleStatus {
    DRAFT("Draft"),
    IN_REVIEW("Under Review"),
    REJECTED("Rejected"),
    PUBLISHED("Published"),
    ARCHIVED("Archived"),
    SCHEDULED("Scheduled for Publication"),
    NEEDS_REVISION("Needs Revision"),
    PENDING_APPROVAL("Pending Approval");

    private final String displayName;

    ArticleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEditable() {
        return this == DRAFT || this == NEEDS_REVISION;
    }

    public boolean isPublishable() {
        return this == IN_REVIEW || this == PENDING_APPROVAL;
    }

    public boolean isViewable() {
        return this == PUBLISHED || this == ARCHIVED;
    }

    public boolean isReviewable() {
        return this == IN_REVIEW || this == PENDING_APPROVAL;
    }

    public boolean canTransitionTo(ArticleStatus newStatus) {
        switch (this) {
            case DRAFT:
                return newStatus == IN_REVIEW || newStatus == ARCHIVED;
            case IN_REVIEW:
                return newStatus == PUBLISHED || newStatus == NEEDS_REVISION || 
                       newStatus == REJECTED || newStatus == PENDING_APPROVAL;
            case NEEDS_REVISION:
                return newStatus == IN_REVIEW || newStatus == DRAFT || newStatus == ARCHIVED;
            case PENDING_APPROVAL:
                return newStatus == PUBLISHED || newStatus == NEEDS_REVISION || 
                       newStatus == REJECTED;
            case PUBLISHED:
                return newStatus == ARCHIVED;
            case REJECTED:
                return newStatus == DRAFT || newStatus == ARCHIVED;
            case SCHEDULED:
                return newStatus == PUBLISHED || newStatus == DRAFT;
            case ARCHIVED:
                return false;
            default:
                return false;
        }
    }

    public String getStatusColor() {
        switch (this) {
            case DRAFT:
                return "text-secondary";
            case IN_REVIEW:
                return "text-warning";
            case PUBLISHED:
                return "text-success";
            case REJECTED:
                return "text-danger";
            case ARCHIVED:
                return "text-muted";
            case SCHEDULED:
                return "text-info";
            case NEEDS_REVISION:
                return "text-warning";
            case PENDING_APPROVAL:
                return "text-primary";
            default:
                return "text-dark";
        }
    }

    public String getBadgeClass() {
        switch (this) {
            case DRAFT:
                return "badge-secondary";
            case IN_REVIEW:
                return "badge-warning";
            case PUBLISHED:
                return "badge-success";
            case REJECTED:
                return "badge-danger";
            case ARCHIVED:
                return "badge-light";
            case SCHEDULED:
                return "badge-info";
            case NEEDS_REVISION:
                return "badge-warning";
            case PENDING_APPROVAL:
                return "badge-primary";
            default:
                return "badge-dark";
        }
    }
}
