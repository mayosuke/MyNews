package jp.mayosuke.android.mynews;

import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class GoogleNewsProviderTest extends ProviderTestCase2<GoogleNewsProvider> {

    public GoogleNewsProviderTest() {
        super(GoogleNewsProvider.class, GoogleNews.AUTHORITY);
    }

    public void testGetMockContentResolver() {
        final MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
    }

    public void testQuery() {
        final MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
    }

    public void testInsert() {
        final MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
    }

    public void testDelete() {
        final MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
    }

    public void testUpdate() {
        final MockContentResolver resolver = getMockContentResolver();
        assertNotNull(resolver);
    }
}
