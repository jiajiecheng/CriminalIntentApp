package com.example.criminalintent.databases;

public class CrimeDBSchema {
    public static final class CrimeTable{
        //表名
        public static final String NAME="crimes";
        public static final class Cols{
            //字段名
            public static final String UUID="uuid";
            public static final String TITLE="title";
            public static final String DATE="date";
            public static final String SOLVED="solved";
            public static final String SUSPECT="suspect";
        }
    }


}
