var booksService = function() {
  var books = [
    {
      _id: 1,
      title: "Les Miserables",
      author: "Victor Hugo",
      image: "//upload.wikimedia.org/wikipedia/commons/6/6c/Jean_Valjean.JPG",
      preview: "In 1815, M. Charles-Francois-Bienvenu Myriel was Bishop of D-- He was an old man of about seventy-five years of age; he had occupied the see of D-- since 1806. Although this detail has no connection whatever with the real substance of what we are about to relate, it will not be superfluous, if merely for the sake of exactness in all points, to mention here the various rumors and remarks which had been in circulation about him from the very moment when he arrived in the diocese. True or false, that which is said of men often occupies as important a place in their lives, and above all in their destinies, as that which they do."
    },
    {
      _id: 2,
      title: "The Book of Five Rings",
      author: "Musashi Miyamoto",
      image: "//upload.wikimedia.org/wikipedia/commons/2/20/Musashi_ts_pic.jpg",
      preview: "I have been many years training in the Way of strategy, called Ni Ten Ichi Ryu, and now I think I will explain it in writing for the first time. It is now during the first ten days of the tenth month in the twentieth year of Kanei (1645). I have climbed mountain Iwato of Higo in Kyushu to pay homage to heaven, pray to Kwannon, and kneel before Buddha. I am a warrior of Harima province, Shinmen Musashi No Kami Fujiwara No Genshin, age sixty years."
    },
    {
      _id: 3,
      title: "Moby Dick",
      author: "Herman Melville",
      image: "//upload.wikimedia.org/wikipedia/commons/3/36/Moby-Dick_FE_title_page.jpg",
      preview: "Call me Ishmael. Some years ago-never mind how long precisely-having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people's hats off-then, I account it high time to get to sea as soon as I can. This is my substitute for pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly take to the ship."
    },
    {
      _id: 4,
      title: "The Hour of the Dragon",
      author: "Robert E. Howard",
      image: "//upload.wikimedia.org/wikipedia/en/6/60/Conan_the_Conqueror.jpg",
      preview: "The long tapers flickered, sending the black shadows wavering along the walls, and the velvet tapestries rippled. Yet there was no wind in the chamber. Four men stood about the ebony table on which lay the green sarcophagus that gleamed like carven jade. In the upraised right hand of each man a curious black candle burned with a weird greenish light. Outside was night and a lost wind moaning among the black trees."
    },
    {
      _id: 5,
      title: "The Brothers Karamazov",
      author: "Fyodor Dostoyevsky",
      image: "//upload.wikimedia.org/wikipedia/commons/2/2d/Dostoevsky-Brothers_Karamazov.jpg",
      preview: "Alexey Fyodorovitch Karamazov was the third son of Fyodor Pavlovitch Karamazov, a land owner well known in our district in his own day, and still remembered among us owing to his gloomy and tragic death, which happened thirteen years ago, and which I shall describe in its proper place. For the present I will only say that this 'landowner'-for so we used to call him, although he hardly spent a day of his life on his own estate-was a strange type, yet one pretty frequently to be met with, a type abject and vicious and at the same time senseless. But he was one of those senseless persons who are very well capable of looking after their worldly affairs, and, apparently, after nothing else. Fyodor Pavlovitch, for instance, began with next to nothing; his estate was of the smallest; he ran to dine at other men's tables, and fastened on them as a toady, yet at his death it appeared that he had a hundred thousand roubles in hard cash. At the same time, he was all his life one of the most senseless, fantastical fellows in the whole district. I repeat, it was not stupidity-the majority of these fantastical fellows are shrewd and intelligent enough-but just senselessness, and a peculiar national form of it."
    }
  ];

  return {
    getAll: function() {
      return books;
    },
    getById: function(id) {
      for (var i = 0; i < books.length; ++i) {
        if (books[i]._id === id) {
          return books[i];
        }
      }
      return null;
    }
  };
};