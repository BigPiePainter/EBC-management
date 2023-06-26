package com.pofa.ebcadmin.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserPermissionEnum {
    public static class PRODUCT_MANAGEMENT{
        @Getter
        private static final String key = "a";
    }
    public static class ORDER_MANAGEMENT{
        @Getter
        private static final String key = "b";
    }
    public static class ACCOUNT_MANAGEMENT{
        @Getter
        private static final String key = "c";
    }
    public static class DEPARTMENT_MANAGEMENT{
        @Getter
        private static final String key = "d";
    }
    public static class TEAM_MANAGEMENT{
        @Getter
        private static final String key = "e";
    }
    public static class PROFIT_REPORT_MANAGEMENT{
        @Getter
        private static final String key = "f";
        public static class SHOW_FULL_PROFIT_REPORT{
            @Getter
            private static final String key = "s";
        }
    }
    public static class FINANCIAL_MANAGEMENT{
        @Getter
        private static final String key = "g";
    }

}
