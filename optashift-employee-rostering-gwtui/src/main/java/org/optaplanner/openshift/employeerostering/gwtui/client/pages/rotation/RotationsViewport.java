/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.openshift.employeerostering.gwtui.client.pages.rotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.grid.CssGridLines;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.grid.Ticks;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.Blob;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.FiniteLinearScale;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.Lane;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.Orientation;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.SubLane;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.Viewport;
import org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.view.BlobView;
import org.optaplanner.openshift.employeerostering.shared.spot.Spot;

import static java.util.Collections.singletonList;
import static org.optaplanner.openshift.employeerostering.gwtui.client.beta.java.model.Orientation.HORIZONTAL;

public class RotationsViewport extends Viewport<Long> {

    private final Integer tenantId;
    private final Supplier<ShiftBlobView> blobViewFactory;
    private final FiniteLinearScale<Long> scale;
    private final CssGridLines gridLines;
    private final Ticks<Long> ticks;
    private final List<Lane<Long>> lanes;

    public RotationsViewport(final Integer tenantId,
                             final Supplier<ShiftBlobView> blobViewFactory,
                             final FiniteLinearScale<Long> scale,
                             final CssGridLines gridLines,
                             final Ticks<Long> ticks,
                             final List<Lane<Long>> lanes) {

        this.tenantId = tenantId;
        this.blobViewFactory = blobViewFactory;
        this.scale = scale;
        this.ticks = ticks;
        this.lanes = lanes;
        this.gridLines = gridLines;
    }

    @Override
    public void drawGridLinesAt(final IsElement target) {
        gridLines.drawAt(target, this);
    }

    @Override
    public void drawTicksAt(final IsElement target) {
        ticks.drawAt(target, this, minutes -> {
            final Long hours = (minutes / 60) % 24;
            if (hours == 0) {
                return "Day " + (minutes / (24 * 60));
            } else {
                return (hours < 10 ? "0" : "") + hours + ":00";
            }
        });
    }

    @Override
    public Lane<Long> newLane() {
        return new SpotLane(new Spot(tenantId, "New spot", new HashSet<>()),
                            new ArrayList<>(singletonList(new SubLane<>(new ArrayList<>()))));
    }

    @Override
    public Blob<Long> newBlob(final Lane<Long> lane,
                              final Long positionInScaleUnits) {

        return new ShiftBlob(positionInScaleUnits, 8L);
    }

    @Override
    public BlobView<Long, ?> newBlobView() {
        return blobViewFactory.get();
    }

    @Override
    public List<Lane<Long>> getLanes() {
        return lanes;
    }

    @Override
    public Long getGridPixelSizeInScreenPixels() {
        return 10L;
    }

    @Override
    public Orientation getOrientation() {
        return HORIZONTAL;
    }

    @Override
    public FiniteLinearScale<Long> getScale() {
        return scale;
    }
}
