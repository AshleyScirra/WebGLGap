// WebGLGap by Ashley Gullen
// www.scirra.com

// Spec:
// http://www.khronos.org/registry/webgl/specs/latest/

(function () {
	var WebGLGapContext = function () {};
	
	// shortcut to WebGLGapContext.prototype
	var ContextProto = WebGLGapContext.prototype;
	
	/* ClearBufferMask */
    ContextProto.DEPTH_BUFFER_BIT               = 0x00000100;
    ContextProto.STENCIL_BUFFER_BIT             = 0x00000400;
    ContextProto.COLOR_BUFFER_BIT               = 0x00004000;
    
    /* BeginMode */
    ContextProto.POINTS                         = 0x0000;
    ContextProto.LINES                          = 0x0001;
    ContextProto.LINE_LOOP                      = 0x0002;
    ContextProto.LINE_STRIP                     = 0x0003;
    ContextProto.TRIANGLES                      = 0x0004;
    ContextProto.TRIANGLE_STRIP                 = 0x0005;
    ContextProto.TRIANGLE_FAN                   = 0x0006;
    
    /* AlphaFunction (not supported in ES20) */
    /*      NEVER */
    /*      LESS */
    /*      EQUAL */
    /*      LEQUAL */
    /*      GREATER */
    /*      NOTEQUAL */
    /*      GEQUAL */
    /*      ALWAYS */
    
    /* BlendingFactorDest */
    ContextProto.ZERO                           = 0;
    ContextProto.ONE                            = 1;
    ContextProto.SRC_COLOR                      = 0x0300;
    ContextProto.ONE_MINUS_SRC_COLOR            = 0x0301;
    ContextProto.SRC_ALPHA                      = 0x0302;
    ContextProto.ONE_MINUS_SRC_ALPHA            = 0x0303;
    ContextProto.DST_ALPHA                      = 0x0304;
    ContextProto.ONE_MINUS_DST_ALPHA            = 0x0305;
    
    /* BlendingFactorSrc */
    /*      ZERO */
    /*      ONE */
    ContextProto.DST_COLOR                      = 0x0306;
    ContextProto.ONE_MINUS_DST_COLOR            = 0x0307;
    ContextProto.SRC_ALPHA_SATURATE             = 0x0308;
    /*      SRC_ALPHA */
    /*      ONE_MINUS_SRC_ALPHA */
    /*      DST_ALPHA */
    /*      ONE_MINUS_DST_ALPHA */
    
    /* BlendEquationSeparate */
    ContextProto.FUNC_ADD                       = 0x8006;
    ContextProto.BLEND_EQUATION                 = 0x8009;
    ContextProto.BLEND_EQUATION_RGB             = 0x8009;   /* same as BLEND_EQUATION */
    ContextProto.BLEND_EQUATION_ALPHA           = 0x883D;
    
    /* BlendSubtract */
    ContextProto.FUNC_SUBTRACT                  = 0x800A;
    ContextProto.FUNC_REVERSE_SUBTRACT          = 0x800B;
    
    /* Separate Blend Functions */
    ContextProto.BLEND_DST_RGB                  = 0x80C8;
    ContextProto.BLEND_SRC_RGB                  = 0x80C9;
    ContextProto.BLEND_DST_ALPHA                = 0x80CA;
    ContextProto.BLEND_SRC_ALPHA                = 0x80CB;
    ContextProto.CONSTANT_COLOR                 = 0x8001;
    ContextProto.ONE_MINUS_CONSTANT_COLOR       = 0x8002;
    ContextProto.CONSTANT_ALPHA                 = 0x8003;
    ContextProto.ONE_MINUS_CONSTANT_ALPHA       = 0x8004;
    ContextProto.BLEND_COLOR                    = 0x8005;
    
    /* Buffer Objects */
    ContextProto.ARRAY_BUFFER                   = 0x8892;
    ContextProto.ELEMENT_ARRAY_BUFFER           = 0x8893;
    ContextProto.ARRAY_BUFFER_BINDING           = 0x8894;
    ContextProto.ELEMENT_ARRAY_BUFFER_BINDING   = 0x8895;
    
    ContextProto.STREAM_DRAW                    = 0x88E0;
    ContextProto.STATIC_DRAW                    = 0x88E4;
    ContextProto.DYNAMIC_DRAW                   = 0x88E8;
    
    ContextProto.BUFFER_SIZE                    = 0x8764;
    ContextProto.BUFFER_USAGE                   = 0x8765;
    
    ContextProto.CURRENT_VERTEX_ATTRIB          = 0x8626;
    
    /* CullFaceMode */
    ContextProto.FRONT                          = 0x0404;
    ContextProto.BACK                           = 0x0405;
    ContextProto.FRONT_AND_BACK                 = 0x0408;
    
    /* DepthFunction */
    /*      NEVER */
    /*      LESS */
    /*      EQUAL */
    /*      LEQUAL */
    /*      GREATER */
    /*      NOTEQUAL */
    /*      GEQUAL */
    /*      ALWAYS */
    
    /* EnableCap */
    /* TEXTURE_2D */
    ContextProto.CULL_FACE                      = 0x0B44;
    ContextProto.BLEND                          = 0x0BE2;
    ContextProto.DITHER                         = 0x0BD0;
    ContextProto.STENCIL_TEST                   = 0x0B90;
    ContextProto.DEPTH_TEST                     = 0x0B71;
    ContextProto.SCISSOR_TEST                   = 0x0C11;
    ContextProto.POLYGON_OFFSET_FILL            = 0x8037;
    ContextProto.SAMPLE_ALPHA_TO_COVERAGE       = 0x809E;
    ContextProto.SAMPLE_COVERAGE                = 0x80A0;
    
    /* ErrorCode */
    ContextProto.NO_ERROR                       = 0;
    ContextProto.INVALID_ENUM                   = 0x0500;
    ContextProto.INVALID_VALUE                  = 0x0501;
    ContextProto.INVALID_OPERATION              = 0x0502;
    ContextProto.OUT_OF_MEMORY                  = 0x0505;
    
    /* FrontFaceDirection */
    ContextProto.CW                             = 0x0900;
    ContextProto.CCW                            = 0x0901;
    
    /* GetPName */
    ContextProto.LINE_WIDTH                     = 0x0B21;
    ContextProto.ALIASED_POINT_SIZE_RANGE       = 0x846D;
    ContextProto.ALIASED_LINE_WIDTH_RANGE       = 0x846E;
    ContextProto.CULL_FACE_MODE                 = 0x0B45;
    ContextProto.FRONT_FACE                     = 0x0B46;
    ContextProto.DEPTH_RANGE                    = 0x0B70;
    ContextProto.DEPTH_WRITEMASK                = 0x0B72;
    ContextProto.DEPTH_CLEAR_VALUE              = 0x0B73;
    ContextProto.DEPTH_FUNC                     = 0x0B74;
    ContextProto.STENCIL_CLEAR_VALUE            = 0x0B91;
    ContextProto.STENCIL_FUNC                   = 0x0B92;
    ContextProto.STENCIL_FAIL                   = 0x0B94;
    ContextProto.STENCIL_PASS_DEPTH_FAIL        = 0x0B95;
    ContextProto.STENCIL_PASS_DEPTH_PASS        = 0x0B96;
    ContextProto.STENCIL_REF                    = 0x0B97;
    ContextProto.STENCIL_VALUE_MASK             = 0x0B93;
    ContextProto.STENCIL_WRITEMASK              = 0x0B98;
    ContextProto.STENCIL_BACK_FUNC              = 0x8800;
    ContextProto.STENCIL_BACK_FAIL              = 0x8801;
    ContextProto.STENCIL_BACK_PASS_DEPTH_FAIL   = 0x8802;
    ContextProto.STENCIL_BACK_PASS_DEPTH_PASS   = 0x8803;
    ContextProto.STENCIL_BACK_REF               = 0x8CA3;
    ContextProto.STENCIL_BACK_VALUE_MASK        = 0x8CA4;
    ContextProto.STENCIL_BACK_WRITEMASK         = 0x8CA5;
    ContextProto.VIEWPORT                       = 0x0BA2;
    ContextProto.SCISSOR_BOX                    = 0x0C10;
    /*      SCISSOR_TEST */
    ContextProto.COLOR_CLEAR_VALUE              = 0x0C22;
    ContextProto.COLOR_WRITEMASK                = 0x0C23;
    ContextProto.UNPACK_ALIGNMENT               = 0x0CF5;
    ContextProto.PACK_ALIGNMENT                 = 0x0D05;
    ContextProto.MAX_TEXTURE_SIZE               = 0x0D33;
    ContextProto.MAX_VIEWPORT_DIMS              = 0x0D3A;
    ContextProto.SUBPIXEL_BITS                  = 0x0D50;
    ContextProto.RED_BITS                       = 0x0D52;
    ContextProto.GREEN_BITS                     = 0x0D53;
    ContextProto.BLUE_BITS                      = 0x0D54;
    ContextProto.ALPHA_BITS                     = 0x0D55;
    ContextProto.DEPTH_BITS                     = 0x0D56;
    ContextProto.STENCIL_BITS                   = 0x0D57;
    ContextProto.POLYGON_OFFSET_UNITS           = 0x2A00;
    /*      POLYGON_OFFSET_FILL */
    ContextProto.POLYGON_OFFSET_FACTOR          = 0x8038;
    ContextProto.TEXTURE_BINDING_2D             = 0x8069;
    ContextProto.SAMPLE_BUFFERS                 = 0x80A8;
    ContextProto.SAMPLES                        = 0x80A9;
    ContextProto.SAMPLE_COVERAGE_VALUE          = 0x80AA;
    ContextProto.SAMPLE_COVERAGE_INVERT         = 0x80AB;
    
    /* GetTextureParameter */
    /*      TEXTURE_MAG_FILTER */
    /*      TEXTURE_MIN_FILTER */
    /*      TEXTURE_WRAP_S */
    /*      TEXTURE_WRAP_T */
    
    ContextProto.NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2;
    ContextProto.COMPRESSED_TEXTURE_FORMATS     = 0x86A3;
    
    /* HintMode */
    ContextProto.DONT_CARE                      = 0x1100;
    ContextProto.FASTEST                        = 0x1101;
    ContextProto.NICEST                         = 0x1102;
    
    /* HintTarget */
    ContextProto.GENERATE_MIPMAP_HINT            = 0x8192;
    
    /* DataType */
    ContextProto.BYTE                           = 0x1400;
    ContextProto.UNSIGNED_BYTE                  = 0x1401;
    ContextProto.SHORT                          = 0x1402;
    ContextProto.UNSIGNED_SHORT                 = 0x1403;
    ContextProto.INT                            = 0x1404;
    ContextProto.UNSIGNED_INT                   = 0x1405;
    ContextProto.FLOAT                          = 0x1406;
    
    /* PixelFormat */
    ContextProto.DEPTH_COMPONENT                = 0x1902;
    ContextProto.ALPHA                          = 0x1906;
    ContextProto.RGB                            = 0x1907;
    ContextProto.RGBA                           = 0x1908;
    ContextProto.LUMINANCE                      = 0x1909;
    ContextProto.LUMINANCE_ALPHA                = 0x190A;
    
    /* PixelType */
    /*      UNSIGNED_BYTE */
    ContextProto.UNSIGNED_SHORT_4_4_4_4         = 0x8033;
    ContextProto.UNSIGNED_SHORT_5_5_5_1         = 0x8034;
    ContextProto.UNSIGNED_SHORT_5_6_5           = 0x8363;
    
    /* Shaders */
    ContextProto.FRAGMENT_SHADER                  = 0x8B30;
    ContextProto.VERTEX_SHADER                    = 0x8B31;
    ContextProto.MAX_VERTEX_ATTRIBS               = 0x8869;
    ContextProto.MAX_VERTEX_UNIFORM_VECTORS       = 0x8DFB;
    ContextProto.MAX_VARYING_VECTORS              = 0x8DFC;
    ContextProto.MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
    ContextProto.MAX_VERTEX_TEXTURE_IMAGE_UNITS   = 0x8B4C;
    ContextProto.MAX_TEXTURE_IMAGE_UNITS          = 0x8872;
    ContextProto.MAX_FRAGMENT_UNIFORM_VECTORS     = 0x8DFD;
    ContextProto.SHADER_TYPE                      = 0x8B4F;
    ContextProto.DELETE_STATUS                    = 0x8B80;
    ContextProto.LINK_STATUS                      = 0x8B82;
    ContextProto.VALIDATE_STATUS                  = 0x8B83;
    ContextProto.ATTACHED_SHADERS                 = 0x8B85;
    ContextProto.ACTIVE_UNIFORMS                  = 0x8B86;
    ContextProto.ACTIVE_ATTRIBUTES                = 0x8B89;
    ContextProto.SHADING_LANGUAGE_VERSION         = 0x8B8C;
    ContextProto.CURRENT_PROGRAM                  = 0x8B8D;
    
    /* StencilFunction */
    ContextProto.NEVER                          = 0x0200;
    ContextProto.LESS                           = 0x0201;
    ContextProto.EQUAL                          = 0x0202;
    ContextProto.LEQUAL                         = 0x0203;
    ContextProto.GREATER                        = 0x0204;
    ContextProto.NOTEQUAL                       = 0x0205;
    ContextProto.GEQUAL                         = 0x0206;
    ContextProto.ALWAYS                         = 0x0207;
    
    /* StencilOp */
    /*      ZERO */
    ContextProto.KEEP                           = 0x1E00;
    ContextProto.REPLACE                        = 0x1E01;
    ContextProto.INCR                           = 0x1E02;
    ContextProto.DECR                           = 0x1E03;
    ContextProto.INVERT                         = 0x150A;
    ContextProto.INCR_WRAP                      = 0x8507;
    ContextProto.DECR_WRAP                      = 0x8508;
    
    /* StringName */
    ContextProto.VENDOR                         = 0x1F00;
    ContextProto.RENDERER                       = 0x1F01;
    ContextProto.VERSION                        = 0x1F02;
    
    /* TextureMagFilter */
    ContextProto.NEAREST                        = 0x2600;
    ContextProto.LINEAR                         = 0x2601;
    
    /* TextureMinFilter */
    /*      NEAREST */
    /*      LINEAR */
    ContextProto.NEAREST_MIPMAP_NEAREST         = 0x2700;
    ContextProto.LINEAR_MIPMAP_NEAREST          = 0x2701;
    ContextProto.NEAREST_MIPMAP_LINEAR          = 0x2702;
    ContextProto.LINEAR_MIPMAP_LINEAR           = 0x2703;
    
    /* TextureParameterName */
    ContextProto.TEXTURE_MAG_FILTER             = 0x2800;
    ContextProto.TEXTURE_MIN_FILTER             = 0x2801;
    ContextProto.TEXTURE_WRAP_S                 = 0x2802;
    ContextProto.TEXTURE_WRAP_T                 = 0x2803;
    
    /* TextureTarget */
    ContextProto.TEXTURE_2D                     = 0x0DE1;
    ContextProto.TEXTURE                        = 0x1702;
    
    ContextProto.TEXTURE_CUBE_MAP               = 0x8513;
    ContextProto.TEXTURE_BINDING_CUBE_MAP       = 0x8514;
    ContextProto.TEXTURE_CUBE_MAP_POSITIVE_X    = 0x8515;
    ContextProto.TEXTURE_CUBE_MAP_NEGATIVE_X    = 0x8516;
    ContextProto.TEXTURE_CUBE_MAP_POSITIVE_Y    = 0x8517;
    ContextProto.TEXTURE_CUBE_MAP_NEGATIVE_Y    = 0x8518;
    ContextProto.TEXTURE_CUBE_MAP_POSITIVE_Z    = 0x8519;
    ContextProto.TEXTURE_CUBE_MAP_NEGATIVE_Z    = 0x851A;
    ContextProto.MAX_CUBE_MAP_TEXTURE_SIZE      = 0x851C;
    
    /* TextureUnit */
    ContextProto.TEXTURE0                       = 0x84C0;
    ContextProto.TEXTURE1                       = 0x84C1;
    ContextProto.TEXTURE2                       = 0x84C2;
    ContextProto.TEXTURE3                       = 0x84C3;
    ContextProto.TEXTURE4                       = 0x84C4;
    ContextProto.TEXTURE5                       = 0x84C5;
    ContextProto.TEXTURE6                       = 0x84C6;
    ContextProto.TEXTURE7                       = 0x84C7;
    ContextProto.TEXTURE8                       = 0x84C8;
    ContextProto.TEXTURE9                       = 0x84C9;
    ContextProto.TEXTURE10                      = 0x84CA;
    ContextProto.TEXTURE11                      = 0x84CB;
    ContextProto.TEXTURE12                      = 0x84CC;
    ContextProto.TEXTURE13                      = 0x84CD;
    ContextProto.TEXTURE14                      = 0x84CE;
    ContextProto.TEXTURE15                      = 0x84CF;
    ContextProto.TEXTURE16                      = 0x84D0;
    ContextProto.TEXTURE17                      = 0x84D1;
    ContextProto.TEXTURE18                      = 0x84D2;
    ContextProto.TEXTURE19                      = 0x84D3;
    ContextProto.TEXTURE20                      = 0x84D4;
    ContextProto.TEXTURE21                      = 0x84D5;
    ContextProto.TEXTURE22                      = 0x84D6;
    ContextProto.TEXTURE23                      = 0x84D7;
    ContextProto.TEXTURE24                      = 0x84D8;
    ContextProto.TEXTURE25                      = 0x84D9;
    ContextProto.TEXTURE26                      = 0x84DA;
    ContextProto.TEXTURE27                      = 0x84DB;
    ContextProto.TEXTURE28                      = 0x84DC;
    ContextProto.TEXTURE29                      = 0x84DD;
    ContextProto.TEXTURE30                      = 0x84DE;
    ContextProto.TEXTURE31                      = 0x84DF;
    ContextProto.ACTIVE_TEXTURE                 = 0x84E0;
    
    /* TextureWrapMode */
    ContextProto.REPEAT                         = 0x2901;
    ContextProto.CLAMP_TO_EDGE                  = 0x812F;
    ContextProto.MIRRORED_REPEAT                = 0x8370;
    
    /* Uniform Types */
    ContextProto.FLOAT_VEC2                     = 0x8B50;
    ContextProto.FLOAT_VEC3                     = 0x8B51;
    ContextProto.FLOAT_VEC4                     = 0x8B52;
    ContextProto.INT_VEC2                       = 0x8B53;
    ContextProto.INT_VEC3                       = 0x8B54;
    ContextProto.INT_VEC4                       = 0x8B55;
    ContextProto.BOOL                           = 0x8B56;
    ContextProto.BOOL_VEC2                      = 0x8B57;
    ContextProto.BOOL_VEC3                      = 0x8B58;
    ContextProto.BOOL_VEC4                      = 0x8B59;
    ContextProto.FLOAT_MAT2                     = 0x8B5A;
    ContextProto.FLOAT_MAT3                     = 0x8B5B;
    ContextProto.FLOAT_MAT4                     = 0x8B5C;
    ContextProto.SAMPLER_2D                     = 0x8B5E;
    ContextProto.SAMPLER_CUBE                   = 0x8B60;
    
    /* Vertex Arrays */
    ContextProto.VERTEX_ATTRIB_ARRAY_ENABLED        = 0x8622;
    ContextProto.VERTEX_ATTRIB_ARRAY_SIZE           = 0x8623;
    ContextProto.VERTEX_ATTRIB_ARRAY_STRIDE         = 0x8624;
    ContextProto.VERTEX_ATTRIB_ARRAY_TYPE           = 0x8625;
    ContextProto.VERTEX_ATTRIB_ARRAY_NORMALIZED     = 0x886A;
    ContextProto.VERTEX_ATTRIB_ARRAY_POINTER        = 0x8645;
    ContextProto.VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889F;
    
    /* Shader Source */
    ContextProto.COMPILE_STATUS                 = 0x8B81;
    
    /* Shader Precision-Specified Types */
    ContextProto.LOW_FLOAT                      = 0x8DF0;
    ContextProto.MEDIUM_FLOAT                   = 0x8DF1;
    ContextProto.HIGH_FLOAT                     = 0x8DF2;
    ContextProto.LOW_INT                        = 0x8DF3;
    ContextProto.MEDIUM_INT                     = 0x8DF4;
    ContextProto.HIGH_INT                       = 0x8DF5;
    
    /* Framebuffer Object. */
    ContextProto.FRAMEBUFFER                    = 0x8D40;
    ContextProto.RENDERBUFFER                   = 0x8D41;
    
    ContextProto.RGBA4                          = 0x8056;
    ContextProto.RGB5_A1                        = 0x8057;
    ContextProto.RGB565                         = 0x8D62;
    ContextProto.DEPTH_COMPONENT16              = 0x81A5;
    ContextProto.STENCIL_INDEX                  = 0x1901;
    ContextProto.STENCIL_INDEX8                 = 0x8D48;
    ContextProto.DEPTH_STENCIL                  = 0x84F9;
    
    ContextProto.RENDERBUFFER_WIDTH             = 0x8D42;
    ContextProto.RENDERBUFFER_HEIGHT            = 0x8D43;
    ContextProto.RENDERBUFFER_INTERNAL_FORMAT   = 0x8D44;
    ContextProto.RENDERBUFFER_RED_SIZE          = 0x8D50;
    ContextProto.RENDERBUFFER_GREEN_SIZE        = 0x8D51;
    ContextProto.RENDERBUFFER_BLUE_SIZE         = 0x8D52;
    ContextProto.RENDERBUFFER_ALPHA_SIZE        = 0x8D53;
    ContextProto.RENDERBUFFER_DEPTH_SIZE        = 0x8D54;
    ContextProto.RENDERBUFFER_STENCIL_SIZE      = 0x8D55;
    
    ContextProto.FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE           = 0x8CD0;
    ContextProto.FRAMEBUFFER_ATTACHMENT_OBJECT_NAME           = 0x8CD1;
    ContextProto.FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL         = 0x8CD2;
    ContextProto.FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 0x8CD3;
    
    ContextProto.COLOR_ATTACHMENT0              = 0x8CE0;
    ContextProto.DEPTH_ATTACHMENT               = 0x8D00;
    ContextProto.STENCIL_ATTACHMENT             = 0x8D20;
    ContextProto.DEPTH_STENCIL_ATTACHMENT       = 0x821A;
    
    ContextProto.NONE                           = 0;
    
    ContextProto.FRAMEBUFFER_COMPLETE                      = 0x8CD5;
    ContextProto.FRAMEBUFFER_INCOMPLETE_ATTACHMENT         = 0x8CD6;
    ContextProto.FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
    ContextProto.FRAMEBUFFER_INCOMPLETE_DIMENSIONS         = 0x8CD9;
    ContextProto.FRAMEBUFFER_UNSUPPORTED                   = 0x8CDD;
    
    ContextProto.FRAMEBUFFER_BINDING            = 0x8CA6;
    ContextProto.RENDERBUFFER_BINDING           = 0x8CA7;
    ContextProto.MAX_RENDERBUFFER_SIZE          = 0x84E8;
    
    ContextProto.INVALID_FRAMEBUFFER_OPERATION  = 0x0506;
    
    /* WebGL-specific enums */
    ContextProto.UNPACK_FLIP_Y_WEBGL            = 0x9240;
    ContextProto.UNPACK_PREMULTIPLY_ALPHA_WEBGL = 0x9241;
    ContextProto.CONTEXT_LOST_WEBGL             = 0x9242;
    ContextProto.UNPACK_COLORSPACE_CONVERSION_WEBGL = 0x9243;
    ContextProto.BROWSER_DEFAULT_WEBGL          = 0x9244;
	
	// For batching commands to PhoneGap
	var commandBatch = [];
	
	function addCommand(action, params)
	{
		commandBatch.push({"action":action,"params":params});
	};
	
	function flushCommands()
	{
		PhoneGap.exec(null, null, "WebGLGap", "commandBatch", commandBatch);
		commandBatch.length = 0;
	};
	
	var nextID = 1;
	
	ContextProto.getContextAttributes = function ()
	{
		// not yet implemented
	};
	
	ContextProto.isContextLost = function ()
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.getSupportedExtensions = function ()
	{
		// not yet implemented
		return [];
	};
	
	ContextProto.getExtension = function (name)
	{
		// not yet implemented
		return null;
	};
	
	ContextProto.activeTexture = function (target)
	{
		addCommand("activeTexture", [target]);
	};
	
	ContextProto.attachShader = function (prog, shader)
	{
		addCommand("attachShader", [prog, shader]);
	};
	
	ContextProto.bindAttribLocation = function (prog, index, name)
	{
		// not yet implemented
	};
	
	ContextProto.bindBuffer = function (type, buffer)
	{
		addCommand("bindBuffer", [type, buffer]);
	};
	
	ContextProto.bindFramebuffer = function (target, framebuffer)
	{
		// not yet implemented
	};
	
	ContextProto.bindRenderbuffer = function (target, renderbuffer)
	{
		// not yet implemented
	};
	
	ContextProto.bindTexture = function (target, texture)
	{
		addCommand("bindTexture", [target, texture]);
	};
	
	ContextProto.blendColor = function (r, g, b, a)
	{
		// not yet implemented
	};
	
	ContextProto.blendEquation = function (mode)
	{
		// not yet implemented
	};
	
	ContextProto.blendEquationSeparate = function (rgb, alpha)
	{
		// not yet implemented
	};
	
	ContextProto.blendFunc = function (src, dest)
	{
		addCommand("blendFunc", [src, dest]);
	};
	
	ContextProto.blendFuncSeparate = function (srcrgb, dstrgb, srcalpha, dstalpha)
	{
		// not yet implemented
	};
	
	// requires array for 'data'
	ContextProto.bufferData = function (type, data, usage)
	{
		addCommand("bufferData", [type, data, usage]);
	};
	
	ContextProto.bufferSubData = function (target, offset, data)
	{
		// not yet implemented
	};
	
	ContextProto.checkFramebufferStatus = function (target)
	{
		// not yet implemented
		return 0;
	};
	
	ContextProto.clear = function (mask)
	{
		addCommand("clear", [mask]);
	};
	
	ContextProto.clearColor = function (r, g, b, a)
	{
		addCommand("clearColor", [r, g, b, a]);
	};
	
	ContextProto.clearDepth = function (depth)
	{
		// not yet implemented
	};
	
	ContextProto.clearStencil = function (s)
	{
		// not yet implemented
	};
	
	ContextProto.colorMask = function (r, g, b, a)
	{
		// not yet implemented
	};
	
	ContextProto.compileShader = function (shader)
	{
		addCommand("compileShader", [shader]);
	};
	
	ContextProto.copyTexImage2D = function ()
	{
		// not yet implemented
	};
	
	ContextProto.copyTexSubImage2D = function ()
	{
		// not yet implemented
	};
	
	ContextProto.createBuffer = function ()
	{
		addCommand("createBuffer", [nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.createFramebuffer = function ()
	{
		return -1; // not yet implemented
	};
	
	ContextProto.createProgram = function ()
	{
		addCommand("createProgram", [nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.createShader = function (type)
	{
		addCommand("createShader", [type, nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.createTexture = function ()
	{
		addCommand("createTexture", [nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.cullFace = function (mode)
	{
		addCommand("cullFace", [mode]);
	};
	
	ContextProto.deleteBuffer = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.deleteFramebuffer = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.deleteProgram = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.deleteRenderbuffer = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.deleteShader = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.deleteTexture = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.depthFunc = function (func)
	{
		// not yet implemented
	};
	
	ContextProto.depthMask = function (flag)
	{
		// not yet implemented
	};
	
	ContextProto.depthRange = function (near, far)
	{
		// not yet implemented
	};
	
	ContextProto.detachShader = function (program, shader)
	{
		// not yet implemented
	};
	
	ContextProto.disable = function (thing)
	{
		addCommand("disable", [thing]);
	};
	
	ContextProto.disableVertexAttribArray = function (index)
	{
		// not yet implemented
	};
	
	ContextProto.drawArrays = function (mode, first, count)
	{
		addCommand("drawArrays", [mode, first, count]);
	};
	
	ContextProto.drawElements = function (mode, count, type, offset)
	{
		addCommand("drawElements", [mode, count, type, offset]);
	};
	
	ContextProto.enable = function (thing)
	{
		addCommand("enable", [thing]);
	};
	
	ContextProto.enableVertexAttribArray = function (pos)
	{
		addCommand("enableVertexAttribArray", [pos]);
	};
	
	ContextProto.finish = function ()
	{
		// not yet implemented
	};
	
	ContextProto.flush = function ()
	{
		addCommand("flush", []);
		flushCommands();		// done
	};
	
	ContextProto.framebufferTexture2D = function (target, attachment, textarget, texture, level)
	{
		// not yet implemented
	};
	
	ContextProto.frontFace = function (mode)
	{
		// not yet implemented
	};
	
	ContextProto.generateMipmap = function (target)
	{
		addCommand("generateMipmap", [target]);
	};
	
	ContextProto.getActiveAttrib = function (prog, index)
	{
		// not yet implemented
	};
	
	ContextProto.getActiveUniform = function (prog, index)
	{
		// not yet implemented
	};
	
	ContextProto.getAttachedShaders = function (prog)
	{
		// not yet implemented
		return [];
	};
	
	ContextProto.getAttribLocation = function (prog, attrib)
	{
		addCommand("getAttribLocation", [prog, attrib, nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.getBufferParameter = function (target, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getParameter = function (pname)
	{
		// not yet implemented
	};
	
	ContextProto.getError = function ()
	{
		// not yet implemented
		return 0;
	};
	
	ContextProto.getFramebufferAttachmentParameter = function (target, attachment, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getProgramParameter = function (prog, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getShaderInfoLog = function (type)
	{
		// not implemented
		return "<getShaderInfoLog not supported>";
	};
	
	ContextProto.getRenderbufferParameter = function (target, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getShaderParameter = function (shader, param)
	{
		// not implemented
		return true;
	};
	
	ContextProto.getShaderPrecisionFormat = function (shadertype, precisiontype)
	{
		// not yet implemented
	};
	
	ContextProto.getShaderSource = function (shader)
	{
		// not yet implemented
	};
	
	ContextProto.getProgramParameter = function (prog, param)
	{
		// not implemented
		return true;
	};
	
	ContextProto.getTexParameter = function (target, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getUniform = function (prog, loc)
	{
		// not yet implemented
	};
	
	ContextProto.getUniformLocation = function (prog, uniform)
	{
		addCommand("getUniformLocation", [prog, uniform, nextID]);
		return {"webglgap_id":nextID++};
	};
	
	ContextProto.getVertexAttrib = function (index, pname)
	{
		// not yet implemented
	};
	
	ContextProto.getVertexAttribOffset = function (index, pname)
	{
		// not yet implemented
	};
	
	ContextProto.hint = function (target, mode)
	{
		// not yet implemented
	};
	
	ContextProto.isBuffer = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isEnabled = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isFramebuffer = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isProgram = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isRenderbuffer = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isShader = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.isTexture = function (x)
	{
		// not yet implemented
		return false;
	};
	
	ContextProto.lineWidth = function (x)
	{
		// not yet implemented
	};
	
	ContextProto.linkProgram = function (prog)
	{
		addCommand("linkProgram", [prog]);
	};
	
	ContextProto.pixelStorei = function (pname, param)
	{
		// ensure param is integer
		if (typeof param === "boolean")
			param = param ? 1 : 0;
		
		addCommand("pixelStorei", [pname, param]);
	};
	
	ContextProto.polygonOffset = function (factor, units)
	{
		// not yet implemented
	};
	
	ContextProto.readPixels = function (x, y, w, h, format, type, pixels)
	{
		// not yet implemented
	};
	
	ContextProto.renderbufferStorage = function (target, internalformat, width, height)
	{
		// not yet implemented
	};
	
	ContextProto.sampleCoverage = function (value, invert)
	{
		// not yet implemented
	};
	
	ContextProto.scissor = function (x, y, width, height)
	{
		// not yet implemented
	};
	
	ContextProto.shaderSource = function (shader, str)
	{
		addCommand("shaderSource", [shader, str]);
	};
	
	ContextProto.stencilFunc = function (func, ref, mask)
	{
		// not yet implemented
	};
	
	ContextProto.stencilFuncSeparate = function (face, func, ref, mask)
	{
		// not yet implemented
	};
	
	ContextProto.stencilMask = function (mask)
	{
		// not yet implemented
	};
	
	ContextProto.stencilMaskSeparate = function (face, mask)
	{
		// not yet implemented
	};
	
	ContextProto.stencilOp = function (fail, zfail, zpass)
	{
		// not yet implemented
	};
	
	ContextProto.stencilOpSeparate = function (face, fail, zfail, zpass)
	{
		// not yet implemented
	};

	// other overloads not supported; expects HTMLImageElement
	ContextProto.texImage2D = function (target, level, internalformat, format, type, image)
	{
		addCommand("texImage2D", [target, level, internalformat, format, type, image.src]);
	};
	
	ContextProto.texParameteri = function (target, pname, param)
	{
		addCommand("texParameteri", [target, pname, param]);
	};
	
	ContextProto.texParameterf = function (target, pname, param)
	{
		// not yet implemented
	};
	
	// No overloads supported
	ContextProto.texSubImage2D = function ()
	{
		// not yet implemented
	};
	
	ContextProto.uniform1f = function (pos, f)
	{
		addCommand("uniform1f", [pos, f]);
	};
	
	ContextProto.uniform1fv = function (pos, f)
	{
		addCommand("uniform1f", [pos, f[0]]);
	};
	
	ContextProto.uniform1i = function (pos, i)
	{
		addCommand("uniform1i", [pos, i]);
	};
	
	ContextProto.uniform1iv = function (pos, i)
	{
		addCommand("uniform1i", [pos, i[0]]);
	};
	
	ContextProto.uniform2f = function (pos, x, y)
	{
		addCommand("uniform2f", [pos, x, y]);
	};
	
	ContextProto.uniform2fv = function (pos, f)
	{
		addCommand("uniform2f", [pos, f[0], f[1]]);
	};
	
	ContextProto.uniform2i = function (pos, x, y)
	{
		addCommand("uniform2i", [pos, x, y]);
	};
	
	ContextProto.uniform2iv = function (pos, i)
	{
		addCommand("uniform2i", [pos, i[0], i[1]]);
	};
	
	ContextProto.uniform3f = function (pos, x, y, z)
	{
		addCommand("uniform3f", [pos, x, y, z]);
	};
	
	ContextProto.uniform3fv = function (pos, f)
	{
		addCommand("uniform3f", [pos, f[0], f[1], f[2]]);
	};
	
	ContextProto.uniform3i = function (pos, x, y, z)
	{
		addCommand("uniform3i", [pos, x, y, z]);
	};
	
	ContextProto.uniform3iv = function (pos, i)
	{
		addCommand("uniform3i", [pos, i[0], i[1], i[2]]);
	};
	
	ContextProto.uniform4f = function (pos, x, y, z, w)
	{
		addCommand("uniform4f", [pos, x, y, z, w]);
	};
	
	ContextProto.uniform4fv = function (pos, f)
	{
		addCommand("uniform4f", [pos, f[0], f[1], f[2], f[3]]);
	};
	
	ContextProto.uniform4i = function (pos, x, y, z, w)
	{
		addCommand("uniform4i", [pos, x, y, z, w]);
	};
	
	ContextProto.uniform4iv = function (pos, i)
	{
		addCommand("uniform4i", [pos, i[0], i[1], i[2], i[3]]);
	};
	
	ContextProto.uniformMatrix2fv = function (pos, transpose, mat)
	{
		// not yet implemented
		//addCommand("uniformMatrix2fv", [pos, transpose, mat]);
	};
	
	ContextProto.uniformMatrix3fv = function (pos, transpose, mat)
	{
		// not yet implemented
		//addCommand("uniformMatrix3fv", [pos, transpose, mat]);
	};
	
	ContextProto.uniformMatrix4fv = function (pos, transpose, mat)
	{
		addCommand("uniformMatrix4fv", [pos, transpose, mat]);
	};
	
	ContextProto.useProgram = function (prog)
	{
		addCommand("useProgram", [prog]);
	};
	
	ContextProto.validateProgram = function (prog)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib1f = function (pos, x)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib1fv = function (pos, x)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib2f = function (pos, x, y)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib2fv = function (pos, x)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib3f = function (pos, x, y, z)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib3fv = function (pos, x)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib4f = function (pos, x, y, z, w)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttrib4fv = function (pos, x)
	{
		// not yet implemented
	};
	
	ContextProto.vertexAttribPointer = function (pos, size, type, normalized, stride, offset)
	{
		addCommand("vertexAttribPointer", [pos, size, type, normalized, stride, offset]);
	};
	
	ContextProto.viewport = function (l, t, r, b)
	{
		// not supported - handled by plugin
	};
	
	//////////////
	// WebGLGap specific
	window.WebGLGap = function () {};

	window.WebGLGap.prototype.createContext = function (attributes)
	{
		PhoneGap.exec(null, null, "WebGLGap", "create", [attributes]);
		return new WebGLGapContext();
	};
	
	PhoneGap.addConstructor(function() {
		PhoneGap.addPlugin("WebGLGap", new WebGLGap());
	});
})();