package com.innometrics.integrationapp.mapping;

import com.innometrics.integrationapp.mapping.adapters.*;

/**
 * Created by killpack on 29.05.15.
 */
public enum DataLevel {
    EVENT_DATA {
        @Override
        InnAdapter getAdapter() {
            return new EventDataAdapter();
        }
    },
    SESSION_DATA {
        @Override
        InnAdapter getAdapter() {
            return new SessionDataAdapter();
        }
    },
    ATTRIBUTE_DATA {
        @Override
        InnAdapter getAdapter() {
            return new AttributeDataAdapter();
        }
    },
    STATIC {
        @Override
        InnAdapter getAdapter() {
            return new StaticAdapter();
        }
    },
    PROFILE_ID {
        @Override
        InnAdapter getAdapter() {
            return new ProfileIdAdapter();
        }
    },
    PROFILE_CREATED {
        @Override
        InnAdapter getAdapter() {
            return new ProfileCreatedAdapter();
        }
    },
    SESSION_CREATED {
        @Override
        InnAdapter getAdapter() {
            return new SessionCreatedAdapter();
        }
    },
    EVENT_CREATED {
        @Override
        InnAdapter getAdapter() {
            return new EventCreatedAdapter();
        }
    },
    EVENT_ID {
        @Override
        InnAdapter getAdapter() {
            return new EventIDAdapter();
        }
    },
    SESSION_ID {
        @Override
        InnAdapter getAdapter() {
            return new SessionIDAdapter();
        }
    },
    EVENT_DEFINITION {
        @Override
        InnAdapter getAdapter() {
            return new EventDefinitionAdapter();
        }
    },
    MACRO {
        @Override
        InnAdapter getAdapter() {
            return new MacroAdapter();
        }
    },
    META {
        @Override
        InnAdapter getAdapter() {
            return null;
        }
    };

    abstract InnAdapter getAdapter();
}
