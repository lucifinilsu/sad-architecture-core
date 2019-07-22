package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IBodyOwner extends Serializable{

    public <D> D body();

}
