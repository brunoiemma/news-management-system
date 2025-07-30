package com.example.demo.model;

public enum Permission {
    // Dashboard permissions
    VIEW_DASHBOARD("View Dashboard", "Access to view dashboard"),

    // User management permissions
    MANAGE_USERS("Manage Users", "Create, edit, and delete users"),
    VIEW_USERS("View Users", "View user list and details"),
    MANAGE_ROLES("Manage Roles", "Create, edit, and delete roles"),
    VIEW_ROLES("View Roles", "View role list and details"),

    // Article management permissions
    CREATE_ARTICLE("Create Article", "Create new articles"),
    EDIT_ARTICLE("Edit Article", "Edit existing articles"),
    DELETE_ARTICLE("Delete Article", "Delete articles"),
    PUBLISH_ARTICLE("Publish Article", "Publish articles"),
    REVIEW_ARTICLE("Review Article", "Review articles before publication"),
    VIEW_ARTICLE("View Article", "View published articles"),

    // Category management permissions
    MANAGE_CATEGORIES("Manage Categories", "Create, edit, and delete categories"),
    VIEW_CATEGORIES("View Categories", "View category list"),

    // Comment management permissions
    MANAGE_COMMENTS("Manage Comments", "Moderate and delete comments"),
    ADD_COMMENT("Add Comment", "Add comments to articles"),
    VIEW_COMMENTS("View Comments", "View article comments"),

    // Statistics and reporting permissions
    VIEW_STATISTICS("View Statistics", "Access to view system statistics"),
    GENERATE_REPORTS("Generate Reports", "Generate system reports"),
    EXPORT_DATA("Export Data", "Export system data"),

    // System management permissions
    MANAGE_SETTINGS("Manage Settings", "Configure system settings"),
    VIEW_LOGS("View Logs", "Access system logs"),
    MANAGE_BACKUP("Manage Backup", "Perform system backups"),

    // API access permissions
    API_ACCESS("API Access", "Access to API endpoints"),
    API_WRITE("API Write", "Write operations via API"),
    API_READ("API Read", "Read operations via API");

    private final String displayName;
    private final String description;

    Permission(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthority() {
        return "PERMISSION_" + this.name();
    }

    public static Permission[] getDefaultPermissions(String role) {
        switch (role.toUpperCase()) {
            case "ROLE_ADMIN":
                return new Permission[]{
                    VIEW_DASHBOARD, MANAGE_USERS, VIEW_USERS, MANAGE_ROLES, VIEW_ROLES,
                    CREATE_ARTICLE, EDIT_ARTICLE, DELETE_ARTICLE, PUBLISH_ARTICLE, REVIEW_ARTICLE, VIEW_ARTICLE,
                    MANAGE_CATEGORIES, VIEW_CATEGORIES, MANAGE_COMMENTS, ADD_COMMENT, VIEW_COMMENTS,
                    VIEW_STATISTICS, GENERATE_REPORTS, EXPORT_DATA, MANAGE_SETTINGS, VIEW_LOGS, MANAGE_BACKUP,
                    API_ACCESS, API_WRITE, API_READ
                };
            case "ROLE_PUBLISHER":
                return new Permission[]{
                    VIEW_DASHBOARD, VIEW_USERS, PUBLISH_ARTICLE, REVIEW_ARTICLE, VIEW_ARTICLE,
                    VIEW_CATEGORIES, MANAGE_COMMENTS, VIEW_COMMENTS, VIEW_STATISTICS
                };
            case "ROLE_REDACTOR":
                return new Permission[]{
                    VIEW_DASHBOARD, CREATE_ARTICLE, EDIT_ARTICLE, VIEW_ARTICLE, REVIEW_ARTICLE,
                    VIEW_CATEGORIES, ADD_COMMENT, VIEW_COMMENTS
                };
            case "ROLE_SUBSCRIBER":
                return new Permission[]{
                    VIEW_DASHBOARD, VIEW_ARTICLE, VIEW_CATEGORIES, ADD_COMMENT, VIEW_COMMENTS
                };
            default:
                return new Permission[]{VIEW_ARTICLE, VIEW_CATEGORIES, VIEW_COMMENTS};
        }
    }

    public static boolean hasPermission(Permission[] permissions, Permission permission) {
        if (permissions == null || permission == null) {
            return false;
        }
        for (Permission p : permissions) {
            if (p == permission) {
                return true;
            }
        }
        return false;
    }

    public static String[] getAuthorities(Permission[] permissions) {
        String[] authorities = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            authorities[i] = permissions[i].getAuthority();
        }
        return authorities;
    }
}
