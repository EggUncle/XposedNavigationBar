/*
 *     Navigation bar function expansion module
 *     Copyright (C) 2017 egguncle cicadashadow@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.egguncle.xposednavigationbar.hook.hookutil;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;

import com.egguncle.xposednavigationbar.hook.util.XpLog;

import static com.egguncle.xposednavigationbar.BuildConfig.DEBUG;
import static de.robv.android.xposed.XposedBridge.log;

public class GesturesListener {
    private static final String TAG = GesturesListener.class.getSimpleName() + ": ";
    private static final long SWIPE_TIMEOUT_MS = 500;
    private static final int MAX_TRACKED_POINTERS = 32;  // max per input system
    private static final int UNTRACKED_POINTER = -1;

    private static final int SWIPE_NONE = 0;
    private static final int SWIPE_FROM_TOP = 1;
    private static final int SWIPE_FROM_BOTTOM = 2;
    private static final int SWIPE_FROM_RIGHT = 3;

    private final int mSwipeStartThreshold;
    private final int mSwipeDistanceThreshold;
    private final Callbacks mCallbacks;
    private final int[] mDownPointerId = new int[MAX_TRACKED_POINTERS];
    private final float[] mDownX = new float[MAX_TRACKED_POINTERS];
    private final float[] mDownY = new float[MAX_TRACKED_POINTERS];
    private final long[] mDownTime = new long[MAX_TRACKED_POINTERS];

    int screenHeight;
    int screenWidth;
    private int mDownPointers;
    private boolean mSwipeFireable;
    private boolean mDebugFireable;

    public GesturesListener(Context context, Callbacks callbacks) {
        mCallbacks = checkNull("callbacks", callbacks);
        Resources resources = checkNull("context", context).getResources();
        mSwipeStartThreshold = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
        mSwipeDistanceThreshold = mSwipeStartThreshold;
    }

    private static <T> T checkNull(String name, T arg) {
        if (arg == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return arg;
    }

    public void onPointerEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mSwipeFireable = true;
                mDebugFireable = true;
                mDownPointers = 0;
                captureDown(event, 0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                captureDown(event, event.getActionIndex());
                if (mDebugFireable) {
                    mDebugFireable = event.getPointerCount() < 5;
                    if (!mDebugFireable) {
                        mCallbacks.onDebug();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSwipeFireable) {
                    final int swipe = detectSwipe(event);
                    mSwipeFireable = swipe == SWIPE_NONE;
                    if (swipe == SWIPE_FROM_TOP) {
                        mCallbacks.onSwipeFromTop();
                    } else if (swipe == SWIPE_FROM_BOTTOM) {
                        int y = (int) event.getY();
                        int navbarH = 200;
                        int screenH = PhoneWindowManagerHook.screenH;
                        XpLog.i("y is " + y + "\n navbarH is " + navbarH + "\n screenH is " + screenH);
                        if (y > (screenH - navbarH))
                            mCallbacks.onSwipeFromBottom();
                    } else if (swipe == SWIPE_FROM_RIGHT) {
                        mCallbacks.onSwipeFromRight();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mSwipeFireable = false;
                mDebugFireable = false;
                break;
            default:
        }
    }

    private void captureDown(MotionEvent event, int pointerIndex) {
        final int pointerId = event.getPointerId(pointerIndex);
        final int i = findIndex(pointerId);

        if (i != UNTRACKED_POINTER) {
            mDownX[i] = event.getX(pointerIndex);
            mDownY[i] = event.getY(pointerIndex);
            mDownTime[i] = event.getEventTime();

        }
    }

    private int findIndex(int pointerId) {
        for (int i = 0; i < mDownPointers; i++) {
            if (mDownPointerId[i] == pointerId) {
                return i;
            }
        }
        if (mDownPointers == MAX_TRACKED_POINTERS || pointerId == MotionEvent.INVALID_POINTER_ID) {
            return UNTRACKED_POINTER;
        }
        mDownPointerId[mDownPointers++] = pointerId;
        return mDownPointers - 1;
    }

    private int detectSwipe(MotionEvent move) {
        final int historySize = move.getHistorySize();
        final int pointerCount = move.getPointerCount();
        for (int p = 0; p < pointerCount; p++) {
            final int pointerId = move.getPointerId(p);
            final int i = findIndex(pointerId);
            if (i != UNTRACKED_POINTER) {
                for (int h = 0; h < historySize; h++) {
                    final long time = move.getHistoricalEventTime(h);
                    final float x = move.getHistoricalX(p, h);
                    final float y = move.getHistoricalY(p, h);
                    final int swipe = detectSwipe(i, time, x, y);
                    if (swipe != SWIPE_NONE) {
                        return swipe;
                    }
                }
                final int swipe = detectSwipe(i, move.getEventTime(), move.getX(p), move.getY(p));
                if (swipe != SWIPE_NONE) {
                    return swipe;
                }
            }
        }
        return SWIPE_NONE;
    }

    private int detectSwipe(int i, long time, float x, float y) {
        final float fromX = mDownX[i];
        final float fromY = mDownY[i];
        final long elapsed = time - mDownTime[i];

        if (fromY <= mSwipeStartThreshold
                && y > fromY + mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_TOP;
        }
        if (fromY >= screenHeight - mSwipeStartThreshold
                && y < fromY - mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_BOTTOM;
        }
        if (fromX >= screenWidth - mSwipeStartThreshold
                && x < fromX - mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_RIGHT;
        }
        return SWIPE_NONE;
    }

    interface Callbacks {
        void onSwipeFromTop();

        void onSwipeFromBottom();

        void onSwipeFromRight();

        void onDebug();
    }
}