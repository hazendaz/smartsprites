/*
 * SmartSprites Project
 *
 * Copyright (C) 2007-2009, Stanisław Osiński.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of  source code must  retain the above  copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following  disclaimer in  the documentation  and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of the SmartSprites Project nor the names of its contributors
 *   may  be used  to endorse  or  promote  products derived   from  this  software
 *   without specific prior written permission.
 *
 * - We kindly request that you include in the end-user documentation provided with
 *   the redistribution and/or in the software itself an acknowledgement equivalent
 *   to  the  following: "This product includes software developed by the SmartSprites
 *   Project."
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  AND
 * ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT LIMITED  TO, THE IMPLIED
 * WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A  PARTICULAR  PURPOSE   ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE  FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES
 * (INCLUDING, BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON
 * ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY,  OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY WAY  OUT OF THE USE  OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package amd;

/**
 * (#)Quantize.java 0.90 9/19/00 Adam Doppelt
 * <p>
 * An efficient color quantization algorithm, adapted from the C++ implementation quantize.c in
 * <a href="http://www.imagemagick.org/">ImageMagick</a>. The pixels for an image are placed into an oct tree. The oct
 * tree is reduced in size, and the pixels from the original image are reassigned to the nodes in the reduced tree.
 * <p>
 * Here is the copyright notice from ImageMagick:
 *
 * <pre>
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 * %  Permission is hereby granted, free of charge, to any person obtaining a    %
 * %  copy of this software and associated documentation files ("ImageMagick"),  %
 * %  to deal in ImageMagick without restriction, including without limitation   %
 * %  the rights to use, copy, modify, merge, publish, distribute, sublicense,   %
 * %  and/or sell copies of ImageMagick, and to permit persons to whom the       %
 * %  ImageMagick is furnished to do so, subject to the following conditions:    %
 * %                                                                             %
 * %  The above copyright notice and this permission notice shall be included in %
 * %  all copies or substantial portions of ImageMagick.                         %
 * %                                                                             %
 * %  The software is provided "as is", without warranty of any kind, express or %
 * %  implied, including but not limited to the warranties of merchantability,   %
 * %  fitness for a particular purpose and noninfringement.  In no event shall   %
 * %  E. I. du Pont de Nemours and Company be liable for any claim, damages or   %
 * %  other liability, whether in an action of contract, tort or otherwise,      %
 * %  arising from, out of or in connection with ImageMagick or the use or other %
 * %  dealings in ImageMagick.                                                   %
 * %                                                                             %
 * %  Except as contained in this notice, the name of the E. I. du Pont de       %
 * %  Nemours and Company shall not be used in advertising or otherwise to       %
 * %  promote the sale, use or other dealings in ImageMagick without prior       %
 * %  written authorization from the E. I. du Pont de Nemours and Company.       %
 * %                                                                             %
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 * </pre>
 *
 * @author <a href="http://www.gurge.com/amd/">Adam Doppelt</a>
 *
 * @version 0.90 19 Sep 2000
 */
public class Quantize {

    /**
     * <pre>
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * %                                                                             %
     * %                                                                             %
     * %                                                                             %
     * %           QQQ   U   U   AAA   N   N  TTTTT  IIIII   ZZZZZ  EEEEE            %
     * %          Q   Q  U   U  A   A  NN  N    T      I        ZZ  E                %
     * %          Q   Q  U   U  AAAAA  N N N    T      I      ZZZ   EEEEE            %
     * %          Q  QQ  U   U  A   A  N  NN    T      I     ZZ     E                %
     * %           QQQQ   UUU   A   A  N   N    T    IIIII   ZZZZZ  EEEEE            %
     * %                                                                             %
     * %                                                                             %
     * %              Reduce the Number of Unique Colors in an Image                 %
     * %                                                                             %
     * %                                                                             %
     * %                           Software Design                                   %
     * %                             John Cristy                                     %
     * %                              July 1992                                      %
     * %                                                                             %
     * %                                                                             %
     * %  Copyright 1998 E. I. du Pont de Nemours and Company                        %
     * %                                                                             %
     * %  Permission is hereby granted, free of charge, to any person obtaining a    %
     * %  copy of this software and associated documentation files ("ImageMagick"),  %
     * %  to deal in ImageMagick without restriction, including without limitation   %
     * %  the rights to use, copy, modify, merge, publish, distribute, sublicense,   %
     * %  and/or sell copies of ImageMagick, and to permit persons to whom the       %
     * %  ImageMagick is furnished to do so, subject to the following conditions:    %
     * %                                                                             %
     * %  The above copyright notice and this permission notice shall be included in %
     * %  all copies or substantial portions of ImageMagick.                         %
     * %                                                                             %
     * %  The software is provided "as is", without warranty of any kind, express or %
     * %  implied, including but not limited to the warranties of merchantability,   %
     * %  fitness for a particular purpose and noninfringement.  In no event shall   %
     * %  E. I. du Pont de Nemours and Company be liable for any claim, damages or   %
     * %  other liability, whether in an action of contract, tort or otherwise,      %
     * %  arising from, out of or in connection with ImageMagick or the use or other %
     * %  dealings in ImageMagick.                                                   %
     * %                                                                             %
     * %  Except as contained in this notice, the name of the E. I. du Pont de       %
     * %  Nemours and Company shall not be used in advertising or otherwise to       %
     * %  promote the sale, use or other dealings in ImageMagick without prior       %
     * %  written authorization from the E. I. du Pont de Nemours and Company.       %
     * %                                                                             %
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * %
     * %  Realism in computer graphics typically requires using 24 bits/pixel to
     * %  generate an image. Yet many graphic display devices do not contain
     * %  the amount of memory necessary to match the spatial and color
     * %  resolution of the human eye. The QUANTIZE program takes a 24 bit
     * %  image and reduces the number of colors so it can be displayed on
     * %  raster device with less bits per pixel. In most instances, the
     * %  quantized image closely resembles the original reference image.
     * %
     * %  A reduction of colors in an image is also desirable for image
     * %  transmission and real-time animation.
     * %
     * %  Function Quantize takes a standard RGB or monochrome images and quantizes
     * %  them down to some fixed number of colors.
     * %
     * %  For purposes of color allocation, an image is a set of n pixels, where
     * %  each pixel is a point in RGB space. RGB space is a 3-dimensional
     * %  vector space, and each pixel, pi, is defined by an ordered triple of
     * %  red, green, and blue coordinates, (ri, gi, bi).
     * %
     * %  Each primary color component (red, green, or blue) represents an
     * %  intensity which varies linearly from 0 to a maximum value, cmax, which
     * %  corresponds to full saturation of that color. Color allocation is
     * %  defined over a domain consisting of the cube in RGB space with
     * %  opposite vertices at (0,0,0) and (cmax,cmax,cmax). QUANTIZE requires
     * %  cmax = 255.
     * %
     * %  The algorithm maps this domain onto a tree in which each node
     * %  represents a cube within that domain. In the following discussion
     * %  these cubes are defined by the coordinate of two opposite vertices:
     * %  The vertex nearest the origin in RGB space and the vertex farthest
     * %  from the origin.
     * %
     * %  The tree's root node represents the the entire domain, (0,0,0) through
     * %  (cmax,cmax,cmax). Each lower level in the tree is generated by
     * %  subdividing one node's cube into eight smaller cubes of equal size.
     * %  This corresponds to bisecting the parent cube with planes passing
     * %  through the midpoints of each edge.
     * %
     * %  The basic algorithm operates in three phases: Classification,
     * %  Reduction, and Assignment. Classification builds a color
     * %  description tree for the image. Reduction collapses the tree until
     * %  the number it represents, at most, the number of colors desired in the
     * %  output image. Assignment defines the output image's color map and
     * %  sets each pixel's color by reclassification in the reduced tree.
     * %  Our goal is to minimize the numerical discrepancies between the original
     * %  colors and quantized colors (quantization error).
     * %
     * %  Classification begins by initializing a color description tree of
     * %  sufficient depth to represent each possible input color in a leaf.
     * %  However, it is impractical to generate a fully-formed color
     * %  description tree in the classification phase for realistic values of
     * %  cmax. If colors components in the input image are quantized to k-bit
     * %  precision, so that cmax= 2k-1, the tree would need k levels below the
     * %  root node to allow representing each possible input color in a leaf.
     * %  This becomes prohibitive because the tree's total number of nodes is
     * %  1 + sum(i=1,k,8k).
     * %
     * %  A complete tree would require 19,173,961 nodes for k = 8, cmax = 255.
     * %  Therefore, to avoid building a fully populated tree, QUANTIZE: (1)
     * %  Initializes data structures for nodes only as they are needed;  (2)
     * %  Chooses a maximum depth for the tree as a function of the desired
     * %  number of colors in the output image (currently log2(colormap size)).
     * %
     * %  For each pixel in the input image, classification scans downward from
     * %  the root of the color description tree. At each level of the tree it
     * %  identifies the single node which represents a cube in RGB space
     * %  containing the pixel's color. It updates the following data for each
     * %  such node:
     * %
     * %    n1: Number of pixels whose color is contained in the RGB cube
     * %    which this node represents;
     * %
     * %    n2: Number of pixels whose color is not represented in a node at
     * %    lower depth in the tree;  initially,  n2 = 0 for all nodes except
     * %    leaves of the tree.
     * %
     * %    Sr, Sg, Sb: Sums of the red, green, and blue component values for
     * %    all pixels not classified at a lower depth. The combination of
     * %    these sums and n2  will ultimately characterize the mean color of a
     * %    set of pixels represented by this node.
     * %
     * %    E: The distance squared in RGB space between each pixel contained
     * %    within a node and the nodes' center. This represents the quantization
     * %    error for a node.
     * %
     * %  Reduction repeatedly prunes the tree until the number of nodes with
     * %  n2 > 0 is less than or equal to the maximum number of colors allowed
     * %  in the output image. On any given iteration over the tree, it selects
     * %  those nodes whose E count is minimal for pruning and merges their
     * %  color statistics upward. It uses a pruning threshold, Ep, to govern
     * %  node selection as follows:
     * %
     * %    Ep = 0
     * %    while number of nodes with (n2 > 0) > required maximum number of colors
     * %      prune all nodes such that E <= Ep
     * %      Set Ep to minimum E in remaining nodes
     * %
     * %  This has the effect of minimizing any quantization error when merging
     * %  two nodes together.
     * %
     * %  When a node to be pruned has offspring, the pruning procedure invokes
     * %  itself recursively in order to prune the tree from the leaves upward.
     * %  n2,  Sr, Sg,  and  Sb in a node being pruned are always added to the
     * %  corresponding data in that node's parent. This retains the pruned
     * %  node's color characteristics for later averaging.
     * %
     * %  For each node, n2 pixels exist for which that node represents the
     * %  smallest volume in RGB space containing those pixel's colors. When n2
     * %  > 0 the node will uniquely define a color in the output image. At the
     * %  beginning of reduction,  n2 = 0  for all nodes except a the leaves of
     * %  the tree which represent colors present in the input image.
     * %
     * %  The other pixel count, n1, indicates the total number of colors
     * %  within the cubic volume which the node represents. This includes n1 -
     * %  n2  pixels whose colors should be defined by nodes at a lower level in
     * %  the tree.
     * %
     * %  Assignment generates the output image from the pruned tree. The
     * %  output image consists of two parts: (1)  A color map, which is an
     * %  array of color descriptions (RGB triples) for each color present in
     * %  the output image;  (2)  A pixel array, which represents each pixel as
     * %  an index into the color map array.
     * %
     * %  First, the assignment phase makes one pass over the pruned color
     * %  description tree to establish the image's color map. For each node
     * %  with n2  > 0, it divides Sr, Sg, and Sb by n2 . This produces the
     * %  mean color of all pixels that classify no lower than this node. Each
     * %  of these colors becomes an entry in the color map.
     * %
     * %  Finally,  the assignment phase reclassifies each pixel in the pruned
     * %  tree to identify the deepest node containing the pixel's color. The
     * %  pixel's value in the pixel array becomes the index of this node's mean
     * %  color in the color map.
     * %
     * %  With the permission of USC Information Sciences Institute, 4676 Admiralty
     * %  Way, Marina del Rey, California  90292, this code was adapted from module
     * %  ALCOLS written by Paul Raveling.
     * %
     * %  The names of ISI and USC are not used in advertising or publicity
     * %  pertaining to distribution of the software without prior specific
     * %  written permission from ISI.
     * %
     * </pre>
     */

    /** The Constant QUICK. */
    static final boolean QUICK = false;

    /** The Constant MAX_RGB. */
    static final int MAX_RGB = 255;

    /** The Constant MAX_NODES. */
    static final int MAX_NODES = 266817;

    /** The Constant MAX_TREE_DEPTH. */
    static final int MAX_TREE_DEPTH = 8;

    /** The squares. These are precomputed in advance. */
    static int[] SQUARES;

    /** The shift. */
    static int[] SHIFT;

    static {
        SQUARES = new int[MAX_RGB + MAX_RGB + 1];
        for (int i = -MAX_RGB; i <= MAX_RGB; i++) {
            SQUARES[i + MAX_RGB] = i * i;
        }

        SHIFT = new int[MAX_TREE_DEPTH + 1];
        for (int i = 0; i < MAX_TREE_DEPTH + 1; ++i) {
            SHIFT[i] = 1 << (15 - i);
        }
    }

    /**
     * Instantiates a new quantize.
     */
    private Quantize() {
        // Prevent Instantiation
    }

    /**
     * Reduce the image to the given number of colors. The pixels are reduced in place.
     *
     * @param pixels
     *            the pixels
     * @param maxColors
     *            the max colors
     *
     * @return The new color palette.
     */
    public static int[] quantizeImage(int[][] pixels, int maxColors) {
        Cube cube = new Cube(pixels, maxColors);
        cube.classification();
        cube.reduction();
        cube.assignment();
        return cube.colormap;
    }

    /**
     * The Class Cube.
     */
    static class Cube {

        /** The pixels. */
        int[][] pixels;

        /** The max colors. */
        int maxColors;

        /** The colormap. */
        int[] colormap;

        /** The root. */
        Node root;

        /** The depth. */
        int depth;

        // counter for the number of colors in the cube. this gets
        /** The colors. */
        // recalculated often.
        int colors;

        /** The nodes. */
        // counter for the number of nodes in the tree
        int nodes;

        /**
         * Instantiates a new cube.
         *
         * @param pixels
         *            the pixels
         * @param maxColors
         *            the max colors
         */
        Cube(int[][] pixels, int maxColors) {
            this.pixels = pixels;
            this.maxColors = maxColors;

            int i = maxColors;
            // tree_depth = log max_colors
            // 4
            for (depth = 1; i != 0; depth++) {
                i /= 4;
            }
            if (depth > 1) {
                --depth;
            }
            if (depth > MAX_TREE_DEPTH) {
                depth = MAX_TREE_DEPTH;
            } else if (depth < 2) {
                depth = 2;
            }

            root = new Node(this);
        }

        /**
         * Procedure Classification begins by initializing a color description tree of sufficient depth to represent
         * each possible input color in a leaf. However, it is impractical to generate a fully-formed color description
         * tree in the classification phase for realistic values of cmax. If colors components in the input image are
         * quantized to k-bit precision, so that cmax= 2k-1, the tree would need k levels below the root node to allow
         * representing each possible input color in a leaf. This becomes prohibitive because the tree's total number of
         * nodes is 1 + sum(i=1,k,8k).
         * <p>
         * A complete tree would require 19,173,961 nodes for k = 8, cmax = 255. Therefore, to avoid building a fully
         * populated tree, QUANTIZE: (1) Initializes data structures for nodes only as they are needed; (2) Chooses a
         * maximum depth for the tree as a function of the desired number of colors in the output image (currently
         * log2(colormap size)).
         * <p>
         * For each pixel in the input image, classification scans downward from the root of the color description tree.
         * At each level of the tree it identifies the single node which represents a cube in RGB space containing It
         * updates the following data for each such node:
         * <p>
         * number_pixels : Number of pixels whose color is contained in the RGB cube which this node represents;
         * <p>
         * unique : Number of pixels whose color is not represented in a node at lower depth in the tree; initially, n2
         * = 0 for all nodes except leaves of the tree.
         * <p>
         * total_red/green/blue : Sums of the red, green, and blue component values for all pixels not classified at a
         * lower depth. The combination of these sums and n2 will ultimately characterize the mean color of a set of
         * pixels represented by this node.
         */
        void classification() {
            int[][] pixels = this.pixels;

            int width = pixels.length;
            int height = pixels[0].length;

            // convert to indexed color
            for (int x = width; x-- > 0;) {
                for (int y = height; y-- > 0;) {
                    int pixel = pixels[x][y];
                    int red = pixel >> 16 & 0xFF;
                    int green = pixel >> 8 & 0xFF;
                    int blue = pixel >> 0 & 0xFF;

                    // a hard limit on the number of nodes in the tree
                    if (nodes > MAX_NODES) {
                        System.out.println("pruning");
                        root.pruneLevel();
                        --depth;
                    }

                    // walk the tree to depth, increasing the
                    // number_pixels count for each node
                    Node node = root;
                    for (int level = 1; level <= depth; ++level) {
                        int id = (red > node.midRed ? 1 : 0) << 0 | (green > node.midGreen ? 1 : 0) << 1
                                | (blue > node.midBlue ? 1 : 0) << 2;
                        if (node.child[id] == null) {
                            new Node(node, id, level);
                        }
                        node = node.child[id];
                        node.numberPixels += SHIFT[level];
                    }

                    ++node.unique;
                    node.totalRed += red;
                    node.totalGreen += green;
                    node.totalBlue += blue;
                }
            }
        }

        /**
         * Reduction.
         * <p>
         * reduction repeatedly prunes the tree until the number of nodes with unique > 0 is less than or equal to the
         * maximum number of colors allowed in the output image.
         * <p>
         * When a node to be pruned has offspring, the pruning procedure invokes itself recursively in order to prune
         * the tree from the leaves upward. The statistics of the node being pruned are always added to the
         * corresponding data in that node's parent. This retains the pruned node's color characteristics for later
         * averaging.
         */
        void reduction() {
            int threshold = 1;
            while (colors > maxColors) {
                colors = 0;
                threshold = root.reduce(threshold, Integer.MAX_VALUE);
            }
        }

        /**
         * The result of a closest color search.
         */
        static class Search {

            /** The distance. */
            int distance;

            /** The color number. */
            int colorNumber;
        }

        /**
         * Assignment.
         * <p>
         * Procedure assignment generates the output image from the pruned tree. The output image consists of two parts:
         * (1) A color map, which is an array of color descriptions (RGB triples) for each color present in the output
         * image; (2) A pixel array, which represents each pixel as an index into the color map array.
         * <p>
         * First, the assignment phase makes one pass over the pruned color description tree to establish the image's
         * color map. For each node with n2 > 0, it divides Sr, Sg, and Sb by n2. This produces the mean color of all
         * pixels that classify no lower than this node. Each of these colors becomes an entry in the color map.
         * <p>
         * Finally, the assignment phase reclassifies each pixel in the pruned tree to identify the deepest node
         * containing the pixel's color. The pixel's value in the pixel array becomes the index of this node's mean
         * color in the color map.
         */
        void assignment() {
            colormap = new int[colors];

            colors = 0;
            root.colormap();

            int[][] pixels = this.pixels;

            int width = pixels.length;
            int height = pixels[0].length;

            Search search = new Search();

            // convert to indexed color
            for (int x = width; x-- > 0;) {
                for (int y = height; y-- > 0;) {
                    int pixel = pixels[x][y];
                    int red = pixel >> 16 & 0xFF;
                    int green = pixel >> 8 & 0xFF;
                    int blue = pixel >> 0 & 0xFF;

                    // walk the tree to find the cube containing that color
                    Node node = root;
                    for (;;) {
                        int id = (red > node.midRed ? 1 : 0) << 0 | (green > node.midGreen ? 1 : 0) << 1
                                | (blue > node.midBlue ? 1 : 0) << 2;
                        if (node.child[id] == null) {
                            break;
                        }
                        node = node.child[id];
                    }

                    if (QUICK) {
                        // if QUICK is set, just use that
                        // node. Strictly speaking, this isn't
                        // necessarily best match.
                        pixels[x][y] = node.colorNumber;
                    } else {
                        // Find the closest color.
                        search.distance = Integer.MAX_VALUE;
                        node.parent.closestColor(red, green, blue, search);
                        pixels[x][y] = search.colorNumber;
                    }
                }
            }
        }

        /**
         * A single Node in the tree.
         */
        static class Node {

            /** The cube. */
            Cube cube;

            /** The parent. */
            // parent node
            Node parent;

            /** The child. */
            // child nodes
            Node[] child;

            /** The nchild. */
            int nchild;

            /** The id. */
            // our index within our parent
            int id;

            /** The level. */
            // our level within the tree
            int level;

            /** The mid red. */
            // our color midpoint
            int midRed;

            /** The mid green. */
            int midGreen;

            /** The mid blue. */
            int midBlue;

            /** The number pixels. */
            // the pixel count for this node and all children
            int numberPixels;

            /** The unique. */
            // the pixel count for this node
            int unique;

            /** The total red. */
            // the sum of all pixels contained in this node
            int totalRed;

            /** The total green. */
            int totalGreen;

            /** The total blue. */
            int totalBlue;

            /** The color number. */
            // used to build the colormap
            int colorNumber;

            /**
             * Instantiates a new node.
             *
             * @param cube
             *            the cube
             */
            Node(Cube cube) {
                this.cube = cube;
                this.parent = this;
                this.child = new Node[8];
                this.id = 0;
                this.level = 0;

                this.numberPixels = Integer.MAX_VALUE;

                this.midRed = (MAX_RGB + 1) >> 1;
                this.midGreen = (MAX_RGB + 1) >> 1;
                this.midBlue = (MAX_RGB + 1) >> 1;
            }

            /**
             * Instantiates a new node.
             *
             * @param parent
             *            the parent
             * @param id
             *            the id
             * @param level
             *            the level
             */
            Node(Node parent, int id, int level) {
                this.cube = parent.cube;
                this.parent = parent;
                this.child = new Node[8];
                this.id = id;
                this.level = level;

                // add to the cube
                ++cube.nodes;
                if (level == cube.depth) {
                    ++cube.colors;
                }

                // add to the parent
                ++parent.nchild;
                parent.child[id] = this;

                // figure out our midpoint
                int bi = 1 << (MAX_TREE_DEPTH - level) >> 1;
                midRed = parent.midRed + ((id & 1) > 0 ? bi : -bi);
                midGreen = parent.midGreen + ((id & 2) > 0 ? bi : -bi);
                midBlue = parent.midBlue + ((id & 4) > 0 ? bi : -bi);
            }

            /**
             * Remove this child node, and make sure our parent absorbs our pixel statistics.
             */
            void pruneChild() {
                --parent.nchild;
                parent.unique += unique;
                parent.totalRed += totalRed;
                parent.totalGreen += totalGreen;
                parent.totalBlue += totalBlue;
                parent.child[id] = null;
                --cube.nodes;
                cube = null;
                parent = null;
            }

            /**
             * Prune the lowest layer of the tree.
             */
            void pruneLevel() {
                if (nchild != 0) {
                    for (int i = 0; i < 8; i++) {
                        if (child[i] != null) {
                            child[i].pruneLevel();
                        }
                    }
                }
                if (level == cube.depth) {
                    pruneChild();
                }
            }

            /**
             * Remove any nodes that have fewer than threshold pixels. Also, as long as we're walking the tree:
             *
             * <pre>
             *  - figure out the color with the fewest pixels
             *  - recalculate the total number of colors in the tree
             * </pre>
             *
             * @param threshold
             *            the threshold
             * @param nextThreshold
             *            the next threshold
             *
             * @return the int
             */
            int reduce(int threshold, int nextThreshold) {
                if (nchild != 0) {
                    for (int i = 0; i < 8; i++) {
                        if (child[i] != null) {
                            nextThreshold = child[i].reduce(threshold, nextThreshold);
                        }
                    }
                }
                if (numberPixels <= threshold) {
                    pruneChild();
                } else {
                    if (unique != 0) {
                        cube.colors++;
                    }
                    if (numberPixels < nextThreshold) {
                        nextThreshold = numberPixels;
                    }
                }
                return nextThreshold;
            }

            /**
             * Colormap.
             * <p>
             * colormap traverses the color cube tree and notes each colormap entry. A colormap entry is any node in the
             * color cube tree where the number of unique colors is not zero.
             */
            void colormap() {
                if (nchild != 0) {
                    for (int i = 0; i < 8; i++) {
                        if (child[i] != null) {
                            child[i].colormap();
                        }
                    }
                }
                if (unique != 0) {
                    int r = (totalRed + (unique >> 1)) / unique;
                    int g = (totalGreen + (unique >> 1)) / unique;
                    int b = (totalBlue + (unique >> 1)) / unique;
                    cube.colormap[cube.colors] = 0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
                    colorNumber = cube.colors++;
                }
            }

            /**
             * Closest color.
             * <p>
             * ClosestColor traverses the color cube tree at a particular node and determines which colormap entry best
             * represents the input color.
             *
             * @param red
             *            the red
             * @param green
             *            the green
             * @param blue
             *            the blue
             * @param search
             *            the search
             */
            void closestColor(int red, int green, int blue, Search search) {
                if (nchild != 0) {
                    for (int i = 0; i < 8; i++) {
                        if (child[i] != null) {
                            child[i].closestColor(red, green, blue, search);
                        }
                    }
                }

                if (unique != 0) {
                    int color = cube.colormap[colorNumber];
                    int distance = distance(color, red, green, blue);
                    if (distance < search.distance) {
                        search.distance = distance;
                        search.colorNumber = colorNumber;
                    }
                }
            }

            /**
             * Figure out the distance between this node and som color.
             *
             * @param color
             *            the color
             * @param r
             *            the r
             * @param g
             *            the g
             * @param b
             *            the b
             *
             * @return the int
             */
            static final int distance(int color, int r, int g, int b) {
                return SQUARES[(color >> 16 & 0xFF) - r + MAX_RGB] + SQUARES[(color >> 8 & 0xFF) - g + MAX_RGB]
                        + SQUARES[(color >> 0 & 0xFF) - b + MAX_RGB];
            }

            @Override
            public String toString() {
                StringBuilder buf = new StringBuilder();
                if (parent == this) {
                    buf.append("root");
                } else {
                    buf.append("node");
                }
                buf.append(' ');
                buf.append(level);
                buf.append(" [");
                buf.append(midRed);
                buf.append(',');
                buf.append(midGreen);
                buf.append(',');
                buf.append(midBlue);
                buf.append(']');
                return new String(buf);
            }
        }
    }
}
