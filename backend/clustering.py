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

#model_path = 'C:\\Users\\lohwm\\Documents\\University of Waterloo\WORK\\CO-OP 1\\INTERN_PACKAGE\\Source Code\\_sample_research_team\\cats_and_dogs\\models\\model.h5'

m = MobileNet()
#m = load_model(model_path)

#out = Dense(64, activation='softmax')
#out = out(m.layers[-1].output)
#m2 = Model(m.input, out)
x = m.predict_generator(img_gen, steps=20, verbose=1)

kmeans = KMeans(n_clusters=2)
pred = kmeans.fit_predict(x)

