package com.innometrics.integrationapp.mapping;

import com.innometrics.integrationapp.mapping.adapters.*;

/**
 * Created by killpack on 29.05.15.
 */
public enum DataLevel {
    EVENT_DATA {
        @Override
        public  InnAdapter getAdapter() {
            return new EventDataAdapter();
        }
    },
    SESSION_DATA {
        @Override
        public  InnAdapter getAdapter() {
            return new SessionDataAdapter();
        }
    },
    ATTRIBUTE_DATA {
        @Override
        public InnAdapter getAdapter() {
            return new AttributeDataAdapter();
        }
    },
    STATIC {
        @Override
        public  InnAdapter getAdapter() {
            return new StaticAdapter();
        }
    },
    PROFILE_ID {
        @Override
        public  InnAdapter getAdapter() {
            return new ProfileIdAdapter();
        }
    },
    PROFILE_CREATED {
        @Override
        public  InnAdapter getAdapter() {
            return new ProfileCreatedAdapter();
        }
    },
    SESSION_CREATED {
        @Override
        public   InnAdapter getAdapter() {
            return new SessionCreatedAdapter();
        }
    },
    EVENT_CREATED {
        @Override
        public   InnAdapter getAdapter() {
            return new EventCreatedAdapter();
        }
    },
    EVENT_ID {
        @Override
        public   InnAdapter getAdapter() {
            return new EventIDAdapter();
        }
    },
    SESSION_ID {
        @Override
        public InnAdapter getAdapter() {
            return new SessionIDAdapter();
        }
    },
    EVENT_DEFINITION {
        @Override
        public   InnAdapter getAdapter() {
            return new EventDefinitionAdapter();
        }
    },
    MACRO {
        @Override
        public  InnAdapter getAdapter() {
            return new MacroAdapter();
        }
    },
    META {
        @Override
        public   InnAdapter getAdapter() {
            return new MetaAdapter();
        }
    };

    public abstract InnAdapter getAdapter();
}
