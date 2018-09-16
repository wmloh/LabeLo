from keras.applications.mobilenet import MobileNet
from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
from keras.layers import Dense, Flatten
from keras.models import Model, load_model
import numpy as np
from sklearn.cluster import KMeans

img_path = './img'
img_data_gen = ImageDataGenerator(rescale=1./ 255)
img_gen = img_data_gen.flow_from_directory(img_path,
                                           target_size=(224, 224),
                                           batch_size=1)

m = MobileNet()
#m = load_model(model_path)

#out = Dense(64, activation='softmax')
#out = out(m.layers[-1].output)
#m2 = Model(m.input, out)
x = m.predict_generator(img_gen, steps=20, verbose=1)

kmeans = KMeans(n_clusters=4)
pred = kmeans.fit_predict(x)
print(pred)
