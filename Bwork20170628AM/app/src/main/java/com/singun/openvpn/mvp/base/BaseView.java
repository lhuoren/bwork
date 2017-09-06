package com.singun.openvpn.mvp.base;

/**
 * Created by access on 2017/8/14.
 */

public interface BaseView<T> {
    /** * 使用fragment作为view时，将activity中的presenter传递给fragment * @param presenter */
    void setPresenter(T presenter);
}

