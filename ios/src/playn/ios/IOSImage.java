/**
 * Copyright 2012 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package playn.ios;

import cli.MonoTouch.CoreGraphics.CGImage;
import cli.MonoTouch.UIKit.UIImage;

import playn.core.gl.GLContext;
import playn.core.gl.Scale;

/**
 * Implements {@link Image} based on a static bitmap.
 */
public class IOSImage extends IOSAbstractImage {

  private final UIImage image;

  public IOSImage (GLContext ctx, UIImage image, Scale scale) {
    super(ctx, scale);
    this.image = image;
  }

  @Override
  public float width() {
    return scale.invScaled(image.get_CGImage().get_Width());
  }

  @Override
  public float height() {
    return scale.invScaled(image.get_CGImage().get_Height());
  }

  @Override
  public Region subImage(float x, float y, float width, float height) {
    return new IOSImageRegion(this, x, y, width, height);
  }

  @Override
  protected CGImage cgImage() {
    return image.get_CGImage();
  }

  @Override
  protected void updateTexture(int tex) {
    ((IOSGLContext) ctx).updateTexture(tex, image);
  }
}
