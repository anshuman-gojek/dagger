package io.odpf.dagger.common.serde.proto.protohandler.typehandler;

import org.apache.flink.api.common.typeinfo.Types;

import com.google.protobuf.Descriptors;
import io.odpf.dagger.consumer.TestBookingLogMessage;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.schema.GroupType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.INT32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntegerPrimitiveTypeHandlerTest {

    @Test
    public void shouldHandleIntegerTypes() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        assertTrue(integerPrimitiveTypeHandler.canHandle());
    }

    @Test
    public void shouldNotHandleTypesOtherThanInteger() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("order_number");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        assertFalse(integerPrimitiveTypeHandler.canHandle());
    }

    @Test
    public void shouldFetchValueForFieldForFieldDescriptorOfTypeInteger() {
        int actualValue = 2;

        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        Object value = integerPrimitiveTypeHandler.parseObject(actualValue);

        assertEquals(actualValue, value);
    }

    @Test
    public void shouldFetchParsedValueForFieldForFieldDescriptorOfTypeInteger() {
        int actualValue = 2;

        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        Object value = integerPrimitiveTypeHandler.parseObject(String.valueOf(actualValue));

        assertEquals(actualValue, value);
    }

    @Test
    public void shouldFetchDefaultValueIfValueNotPresentForFieldDescriptorOfTypeInteger() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        Object value = integerPrimitiveTypeHandler.parseObject(null);

        assertEquals(0, value);
    }

    @Test
    public void shouldReturnTypeInformation() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        assertEquals(Types.INT, integerPrimitiveTypeHandler.getTypeInformation());
    }

    @Test
    public void shouldReturnArrayTypeInformation() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        assertEquals(Types.PRIMITIVE_ARRAY(Types.INT), integerPrimitiveTypeHandler.getArrayType());
    }

    @Test
    public void shouldReturnArrayValues() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        ArrayList<Integer> inputValues = new ArrayList<>(Arrays.asList(1, 2, 3));
        Object actualValues = integerPrimitiveTypeHandler.getArray(inputValues);

        assertArrayEquals(new int[]{1, 2, 3}, (int[]) actualValues);
    }

    @Test
    public void shouldReturnEmptyArrayOnNull() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");
        IntegerPrimitiveTypeHandler integerPrimitiveTypeHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        Object actualValues = integerPrimitiveTypeHandler.getArray(null);

        assertEquals(0, ((int[]) actualValues).length);
    }

    @Test
    public void shouldFetchParsedValueForFieldOfTypeIntegerInSimpleGroup() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");

        GroupType parquetSchema = org.apache.parquet.schema.Types.requiredGroup()
                .required(INT32).named("cancel_reason_id")
                .named("TestGroupType");
        SimpleGroup simpleGroup = new SimpleGroup(parquetSchema);
        simpleGroup.add("cancel_reason_id", 34);

        IntegerPrimitiveTypeHandler integerHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);
        Object actualValue = integerHandler.parseSimpleGroup(simpleGroup);

        assertEquals(34, actualValue);
    }

    @Test
    public void shouldFetchDefaultValueIfFieldNotPresentInSimpleGroup() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");

        GroupType parquetSchema = org.apache.parquet.schema.Types.requiredGroup()
                .required(INT32).named("some-other-field")
                .named("TestGroupType");
        SimpleGroup simpleGroup = new SimpleGroup(parquetSchema);
        IntegerPrimitiveTypeHandler integerHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);

        Object actualValue = integerHandler.parseSimpleGroup(simpleGroup);

        assertEquals(0, actualValue);
    }

    @Test
    public void shouldFetchDefaultValueIfFieldNotInitializedWithAValueInSimpleGroup() {
        Descriptors.FieldDescriptor fieldDescriptor = TestBookingLogMessage.getDescriptor().findFieldByName("cancel_reason_id");

        /* The field is added to the schema but not assigned a value */
        GroupType parquetSchema = org.apache.parquet.schema.Types.requiredGroup()
                .required(INT32).named("cancel_reason_id")
                .named("TestGroupType");
        SimpleGroup simpleGroup = new SimpleGroup(parquetSchema);
        IntegerPrimitiveTypeHandler integerHandler = new IntegerPrimitiveTypeHandler(fieldDescriptor);

        Object actualValue = integerHandler.parseSimpleGroup(simpleGroup);

        assertEquals(0, actualValue);
    }
}
