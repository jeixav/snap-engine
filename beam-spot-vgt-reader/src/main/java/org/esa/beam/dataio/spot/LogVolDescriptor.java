package org.esa.beam.dataio.spot;

import com.bc.ceres.binding.PropertySet;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class LogVolDescriptor {
    private final PropertySet propertySet;
    private final String productId;

    private static final double PIXEL_CENTER = 0.0;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);

    public LogVolDescriptor(Reader reader) throws IOException {
        this.propertySet = SpotVgtProductReaderPlugIn.readKeyValuePairs(reader);
        this.productId = getValueString("PRODUCT_ID");
    }

    public PropertySet getPropertySet() {
        return propertySet;
    }

    public String getValueString(String key) {
        return (String) propertySet.getValue(key);
    }

    Integer getValueInteger(String key) {
        final String value = getValueString(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // ?
            }
        }
        return null;
    }

    Double getValueDouble(String key) {
        final String value = getValueString(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // ?
            }
        }
        return null;
    }

    Date getValueDate(String s) {
        String value = getValueString(s);
        if (value != null) {
            try {
                return DATE_FORMAT.parse(value);
            } catch (ParseException e) {
                // ?
            }
        }
        return null;
    }

    Date getValueDate(String s1, String s2) {
        String value1 = getValueString(s1);
        String value2 = getValueString(s2);
        if (value1 != null && value2 != null) {
            try {
                return DATE_FORMAT.parse(value1 + value2);
            } catch (ParseException e) {
                // ?
            }
        }
        return null;
    }

    public String getProductId() {
        return productId;
    }

    public GeoCoding getGeoCoding() {
        Double meridianOrigin = getValueDouble("MERIDIAN_ORIGIN");
        if (meridianOrigin != null && meridianOrigin != 0.0) {
            return null;
        }

        String geodeticSystName = getValueString("GEODETIC_SYST_NAME");
        if (geodeticSystName != null && !geodeticSystName.equals("WGS 1984")) {
            return null;
        }

        String mapProjUnit = getValueString("MAP_PROJ_UNIT");
        if (mapProjUnit != null && !mapProjUnit.equals("DEGREES")) {
            return null;
        }

        Rectangle imageBounds = getImageBounds();
        if (imageBounds == null) {
            return null;
        }

        Double pixelSize = getValueDouble("MAP_PROJ_RESOLUTION");
        Double upperLeftLat = getValueDouble("GEO_UPPER_LEFT_LAT");
        Double upperLeftLon = getValueDouble("GEO_UPPER_LEFT_LONG");
        if (pixelSize != null
                && upperLeftLat != null
                && upperLeftLon != null) {
            try {
                AffineTransform transform = new AffineTransform();
                transform.translate(upperLeftLon, upperLeftLat);
                transform.scale(pixelSize, -pixelSize);
                transform.translate(-PIXEL_CENTER, -PIXEL_CENTER);
                return new CrsGeoCoding(DefaultGeographicCRS.WGS84, imageBounds, transform);
            } catch (TransformException e) {
                // ?
            } catch (FactoryException e) {
                // ?
            }
        }

        return null;
    }

    public Date getStartDate() {
        Date date = getValueDate("SYNTHESIS_FIRST_DATE");
        if (date == null) {
            date = getValueDate("SEGM_FIRST_DATE", "SEGM_FIRST_TIME");
        }
        return date;
    }

    public Date getEndDate() {
        Date date = getValueDate("SYNTHESIS_LAST_DATE");
        if (date == null) {
            date = getValueDate("SEGM_LAST_DATE", "SEGM_LAST_TIME");
        }
        return date;
    }

    Rectangle getImageBounds() {
        Integer upperLeftCol = getValueInteger("IMAGE_UPPER_LEFT_COL");
        Integer upperLeftRow = getValueInteger("IMAGE_UPPER_LEFT_ROW");
        Integer lowerRightCol = getValueInteger("IMAGE_LOWER_RIGHT_COL");
        Integer lowerRightRow = getValueInteger("IMAGE_LOWER_RIGHT_ROW");
        if (upperLeftCol != null
                && upperLeftRow != null
                && lowerRightCol != null
                && lowerRightRow != null) {
                return new Rectangle(upperLeftCol - 1, upperLeftRow - 1,
                                                     lowerRightCol - upperLeftCol + 1,
                                                     lowerRightRow - upperLeftRow + 1);
        }
        return null;
    }

}
