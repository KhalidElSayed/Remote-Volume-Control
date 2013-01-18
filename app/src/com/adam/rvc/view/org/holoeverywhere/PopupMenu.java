package com.adam.rvc.view.org.holoeverywhere;


import android.content.Context;
import android.view.View;
import com.actionbarsherlock.internal.view.menu.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    public interface OnDismissListener {
        public void onDismiss(PopupMenu menu);
    }

    public interface OnMenuItemClickListener {
        public boolean onMenuItemClick(MenuItem item);
    }

    private final View mAnchor;
    private final Context mContext;
    private final MenuBuilder mMenu;
    private final RVCMenuPopupHelper mPopup;

    private OnDismissListener mDismissListener;
    private OnMenuItemClickListener mMenuItemClickListener;

    public PopupMenu(Context context, View anchor) {
        mContext = context;
        mMenu = new MenuBuilder(context);
        mMenu.setCallback(this);
        mAnchor = anchor;
        mPopup = new RVCMenuPopupHelper(context, mMenu, mAnchor);
        mPopup.setCallback(this);
    }

    public void dismiss() {
        mPopup.dismiss();
    }

    public Menu getMenu() {
        return mMenu;
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(mContext);
    }

    public void inflate(int menuRes) {
        getMenuInflater().inflate(menuRes, mMenu);
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (mDismissListener != null) {
            mDismissListener.onDismiss(this);
        }
    }

    public void onCloseSubMenu(SubMenuBuilder menu) {
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        if (mMenuItemClickListener != null) {
            return mMenuItemClickListener.onMenuItemClick(item);
        }
        return false;
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {
    }

    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        if (subMenu == null) {
            return false;
        }
        if (!subMenu.hasVisibleItems()) {
            return true;
        }
        new RVCMenuPopupHelper(mContext, subMenu, mAnchor).show();
        return true;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mDismissListener = listener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mMenuItemClickListener = listener;
    }

    public void show() {
        mPopup.show();
    }
}