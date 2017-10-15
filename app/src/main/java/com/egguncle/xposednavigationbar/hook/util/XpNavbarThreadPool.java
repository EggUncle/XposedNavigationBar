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

package com.egguncle.xposednavigationbar.hook.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by egguncle on 17-10-15.
 */

public class XpNavbarThreadPool extends ThreadPoolExecutor {
    private static XpNavbarThreadPool instance = new XpNavbarThreadPool(5);

    private XpNavbarThreadPool(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    }

    public static XpNavbarThreadPool getInstance() {
        return instance;
    }
}
