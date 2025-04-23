package com.sbit.chatify.constant;

public interface ServicesConstant {
    String DEFAULT_PROFILE_IMAGE = "https://res.cloudinary.com/dmd2a0bgi/image/upload/v1563641797/user_ibif10.png";
    String CLOUDINARY_CLOUD_NAME = "dmd2a0bgi";
    String CLOUDINARY_API_KEY = "488992772562963";
    String CLOUDINARY_API_SECRET_KEY = "7qA11xwivWBD7CgPeioQ7sRI2U8";
    String JWT_SECRET_KEY = "56f9eb23018efca25d33b51967e0034c96f8f83ae053c03651d60c6dbb2371a0d998b2718b6" +
            "5c71cb391d8ef043cac152f1de253ac1272111b585721cf4abaacc64d056cf662cb2f016d1a20d99fbc147b61c0" +
            "e45e9999213d2564742209e1d0e3f87043274b43e9c58c36b662cd325ac3916fe7b94808138ed72f4214d27140d" +
            "2a245d7305bb784c3cf77b79bbf5cb9e0927351ef4fd3a9cb9971c2b83036527eeb10e9ffa163de2819d9e80ca5" +
            "0b9255b06b3cb097922da28dde2a56956770c08377037e7d9a991a6584fa46d48bda8aecb4ea194b617ee68a3be" +
            "d6df2975605e1a80ba563de3b2e8777048347fca75ff97b2c15494c344b288c107dfff459";
    int JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 1; // 1 hour

}
