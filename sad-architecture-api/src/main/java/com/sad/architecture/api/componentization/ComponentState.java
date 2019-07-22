package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/31 0031.
 */

public enum ComponentState implements Serializable{
    UNWORKED,
    WORKING,
    DONE,
    INTERCEPTED,
    EXCEPTION,
    CANCELED;
}
