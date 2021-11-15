package org.carrot2.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link FileUtils}.
 */
class FileUtilsTest
{
    @Test
    void canonicalizeEmpty()
    {
        assertEquals("", FileUtils.canonicalize("", "/"));
    }

    @Test
    void canonicalizeOneSegment()
    {
        assertEquals("file", FileUtils.canonicalize("file", "/"));
    }

    @Test
    void canonicalizeTwoSegmentsCanonical()
    {
        assertEquals("path/file", FileUtils.canonicalize("path/file", "/"));
    }

    @Test
    void canonicalizeTwoSegmentsNonCanonical()
    {
        assertEquals("", FileUtils.canonicalize("path/..", "/"));
    }

    @Test
    void canonicalizeTwoSegmentsNonCanonicalTrailingSeparator()
    {
        assertEquals("", FileUtils.canonicalize("path/../", "/"));
    }

    @Test
    void canonicalizeMoreSegmentsNonCanonical()
    {
        assertEquals("/longer/file/actual/file", FileUtils.canonicalize("/longer/file/path/../actual/file", "/"));
    }

    @Test
    void canonicalizeMoreParents()
    {
        assertEquals("/longer/actual/file", FileUtils.canonicalize("/longer/file/path/../../actual/file", "/"));
    }

    @Test
    void canonicalizeCanonicalStartingWithParent()
    {
        assertEquals("../../img/sprite.png", FileUtils.canonicalize("../../img/sprite.png", "/"));
    }
}
